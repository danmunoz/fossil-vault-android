# FossilVault Firebase Data Management - Platform-Agnostic Guide

## Architecture Overview

The FossilVault data management system implements a clean, protocol-based architecture that separates data access concerns from business logic. This design enables easy testing, platform portability, and service provider swapping while maintaining real-time data synchronization across all clients.

## Core Architecture Components

### 1. Protocol/Interface Abstractions

#### DatabaseManaging Protocol
Defines the contract for all database operations:

```typescript
interface DatabaseManaging {
    // Reactive Data Streams
    specimens: Observable<Specimen[]>
    tags: Observable<Tag[]>
    profile: Observable<UserProfile | null>
    
    // Specimen Operations
    save(specimen: Specimen): Promise<void>
    update(specimen: Specimen): Promise<void>
    getSpecimen(identifier: string): Promise<Specimen | null>
    getAllSpecimens(): Promise<Specimen[]>
    deleteSpecimen(identifier: string): Promise<void>
    
    // Tag Operations
    save(tag: Tag): Promise<void>
    
    // Profile Operations
    updateProfile(profile: UserProfile, imageUrl?: StoredImage): Promise<void>
    
    // Utility
    clearAllData(): void
}
```

#### ImageStoring Protocol
Handles all image storage operations:

```typescript
interface ImageStoring {
    uploadImage(imageData: Uint8Array, folder: string): Promise<StoredImage>
    uploadImages(imagesData: Uint8Array[], folder: string): Promise<StoredImage[]>
    deleteImage(image: StoredImage): Promise<void>
    deleteImages(images: StoredImage[]): Promise<void>
}
```

### 2. Firebase Implementation Architecture

#### FirestoreDataManager
Primary database implementation using Firebase Firestore:

```typescript
class FirestoreDataManager implements DatabaseManaging {
    private db: Firestore
    private imageStorageManager: ImageStoring
    private authenticationManager: AuthenticationManager
    
    // Reactive subjects for real-time updates
    private specimensSubject: BehaviorSubject<Specimen[]>
    private tagsSubject: BehaviorSubject<Tag[]>
    private profileSubject: BehaviorSubject<UserProfile | null>
    
    // Firestore listeners for real-time sync
    private specimenListener: Unsubscribe | null
    private profileListener: Unsubscribe | null
    private tagListener: Unsubscribe | null
}
```

#### FirebaseStorageService
Image storage implementation using Firebase Storage:

```typescript
class FirebaseStorageService implements ImageStoring {
    private storage: FirebaseStorage
    private readonly specimenFolder = "specimen"
    private readonly profileFolder = "profile"
}
```

## Data Organization & Collections

### Firestore Collection Structure

```
/specimens/{specimenId}
    - Specimen documents with userId filtering
    - Real-time listeners with user scope
    - Automatic cleanup on user sign-out

/tags/{userId}_{tagName}
    - Composite key for user-specific tags
    - Automatic userId injection on save
    - Lowercase name normalization

/users/{userId}
    - UserProfile documents
    - Profile image references
    - App settings storage
```

### Firebase Storage Structure

```
/specimen/{userId}/{imageId}.{ext}
    - User-scoped specimen images
    - Automatic format detection (jpg/png/gif)
    - UUID-based naming for uniqueness

/profile/{userId}/{imageId}.{ext}
    - User profile pictures
    - Single image per user (replaces existing)
    - Same naming convention as specimens
```

## Real-Time Data Synchronization

### Reactive Streams Architecture

The system uses reactive programming patterns to provide real-time updates:

```typescript
// Setup real-time listeners on authentication
private setupListeners(userId: string): void {
    // Specimens listener
    this.specimenListener = this.db.collection('specimens')
        .where('userId', '==', userId)
        .onSnapshot((snapshot) => {
            const specimens = snapshot.docs.map(doc => doc.data() as Specimen)
            this.specimensSubject.next(specimens)
        })
    
    // Tags listener  
    this.tagListener = this.db.collection('tags')
        .where('userId', '==', userId)
        .onSnapshot((snapshot) => {
            const tags = snapshot.docs.map(doc => doc.data() as Tag)
            this.tagsSubject.next(tags)
        })
    
    // Profile listener
    this.profileListener = this.db.collection('users')
        .where('userId', '==', userId)
        .onSnapshot((snapshot) => {
            const profiles = snapshot.docs.map(doc => doc.data() as UserProfile)
            this.profileSubject.next(profiles[0] || null)
        })
}
```

### Authentication State Integration

Data synchronization is tightly coupled with authentication state:

```typescript
// Monitor authentication changes
authenticationManager.isAuthenticated.subscribe((isAuthenticated) => {
    if (isAuthenticated) {
        this.setupListeners()
    } else {
        this.removeListeners()
        this.clearLocalData()
    }
})
```

## Data Operations

### Specimen Management

#### Create/Update Operations
```typescript
async save(specimen: Specimen): Promise<void> {
    // 1. Handle local images (file:// URLs)
    const localImages = specimen.imageUrls.filter(img => 
        img.url.startsWith('file://'))
    
    if (localImages.length > 0) {
        // 2. Convert local URLs to binary data
        const imageDataArray = await Promise.all(
            localImages.map(img => this.loadImageData(img.url))
        )
        
        // 3. Upload images to cloud storage
        const remoteImages = await this.imageStorageManager
            .uploadImages(imageDataArray, 'specimen')
        
        // 4. Update specimen with remote URLs
        const existingRemoteUrls = specimen.imageUrls.filter(img => 
            !img.url.startsWith('file://'))
        specimen.imageUrls = [...existingRemoteUrls, ...remoteImages]
    }
    
    // 5. Save to Firestore
    await this.db.collection('specimens')
        .doc(specimen.id)
        .set(specimen)
    
    // Real-time listener automatically updates UI
}
```

#### Update with Image Management
```typescript
async update(specimen: Specimen): Promise<void> {
    // 1. Get existing specimen for comparison
    const existingSpecimen = await this.getSpecimen(specimen.id)
    if (!existingSpecimen) {
        return this.save(specimen) // Treat as new if not found
    }
    
    // 2. Find removed images
    const imagesToRemove = existingSpecimen.imageUrls.filter(oldImg =>
        !specimen.imageUrls.includes(oldImg) && 
        !oldImg.url.startsWith('file://'))
    
    // 3. Delete removed images from storage
    if (imagesToRemove.length > 0) {
        await this.imageStorageManager.deleteImages(imagesToRemove)
    }
    
    // 4. Proceed with normal save operation
    await this.save(specimen)
}
```

#### Delete Operations
```typescript
async deleteSpecimen(identifier: string): Promise<void> {
    // 1. Get specimen to clean up associated images
    const specimen = await this.getSpecimen(identifier)
    
    // 2. Delete all associated images
    if (specimen && specimen.imageUrls.length > 0) {
        await this.imageStorageManager.deleteImages(specimen.imageUrls)
    }
    
    // 3. Delete Firestore document
    await this.db.collection('specimens').doc(identifier).delete()
    
    // Real-time listener automatically updates UI
}
```

### Tag Management

#### Auto-normalization and User Scoping
```typescript
async save(tag: Tag): Promise<void> {
    const userId = await this.getUserId()
    
    // Normalize and scope the tag
    const normalizedTag = {
        ...tag,
        name: tag.name.toLowerCase(),
        userId: userId
    }
    
    // Use composite key for user-specific tags
    const documentId = `${userId}_${normalizedTag.name}`
    
    await this.db.collection('tags')
        .doc(documentId)
        .set(normalizedTag)
}
```

### Profile Management

#### Profile with Image Handling
```typescript
async updateProfile(profile: UserProfile, imageUrl?: StoredImage): Promise<void> {
    let updatedProfile = { ...profile }
    
    // Handle profile image upload
    if (imageUrl && imageUrl.url.startsWith('file://')) {
        // 1. Load local image data
        const imageData = await this.loadImageData(imageUrl.url)
        
        // 2. Upload to profile folder
        const remoteImage = await this.imageStorageManager
            .uploadImage(imageData, 'profile')
        
        // 3. Update profile with remote URL
        updatedProfile.picture = remoteImage
    }
    
    // 4. Save to Firestore
    await this.db.collection('users')
        .doc(profile.userId)
        .set(updatedProfile)
}
```

## Image Management

### Automatic Format Detection
```typescript
private getImageExtension(data: Uint8Array): string {
    // Check file signature/magic bytes
    const header = Array.from(data.slice(0, 3))
        .map(byte => byte.toString(16).padStart(2, '0'))
        .join('')
    
    switch (header) {
        case 'ffd8ff': return 'jpg'  // JPEG
        case '89504e': return 'png'  // PNG  
        case '474946': return 'gif'  // GIF
        default: return 'jpg'        // Default fallback
    }
}
```

### Concurrent Upload Operations
```typescript
async uploadImages(imagesData: Uint8Array[], folder: string): Promise<StoredImage[]> {
    // Upload all images concurrently for better performance
    const uploadPromises = imagesData.map(imageData => 
        this.uploadImage(imageData, folder))
    
    return Promise.all(uploadPromises)
}

async deleteImages(images: StoredImage[]): Promise<void> {
    // Delete all images concurrently
    const deletePromises = images.map(image => 
        this.deleteImage(image))
    
    await Promise.all(deletePromises)
}
```

## Performance Optimizations

### Firestore Configuration
```typescript
// Persistent disk cache for offline support
const firestoreSettings = {
    cacheSizeBytes: 50 * 1024 * 1024, // 50MB cache
    persistence: true
}

// Development emulator support
if (isDevelopment && serverIP) {
    firestoreSettings.host = `${serverIP}:8282`
    firestoreSettings.ssl = false
}
```

### Listener Management
```typescript
// Proper cleanup to prevent memory leaks
private removeListeners(): void {
    this.specimenListener?.()
    this.specimenListener = null
    
    this.profileListener?.()
    this.profileListener = null
    
    this.tagListener?.()
    this.tagListener = null
}

// Cleanup on destruction
destructor(): void {
    this.removeListeners()
    this.cancellables.forEach(subscription => subscription.unsubscribe())
}
```

## Error Handling

### Custom Error Types
```typescript
enum FirebaseStorageError {
    noUserLoggedIn = "No user is currently logged in"
    invalidImageData = "Invalid image data provided"  
    failedToGetDownloadURL = "Failed to get download URL for uploaded image"
}

enum ImageStoringError {
    uploadFailed = "Image upload failed"
    deleteFailed = "Image deletion failed"
    invalidImageData = "Invalid image data"
    configurationError = "Storage configuration error"
}
```

### Error Recovery Patterns
```typescript
// Graceful error handling with user feedback
try {
    await this.save(specimen)
} catch (error) {
    if (error instanceof FirebaseStorageError) {
        // Handle storage-specific errors
        throw new Error("Failed to save images. Please check your connection.")
    } else {
        // Handle general Firestore errors
        throw new Error("Failed to save specimen. Please try again.")
    }
}
```

## Development & Testing Support

### Emulator Integration
```typescript
// Automatic emulator detection for development
constructor(serverIP?: string) {
    if (serverIP) {
        // Use Firebase emulators
        Firestore.useEmulator(serverIP, 8282)
        Storage.useEmulator(serverIP, 9199)
        Auth.useEmulator(serverIP, 9099)
    }
}
```

### Mock Implementations
```typescript
// Mock database for testing
class MockDatabaseManager implements DatabaseManaging {
    private mockSpecimens: BehaviorSubject<Specimen[]>
    private mockTags: BehaviorSubject<Tag[]>
    private mockProfile: BehaviorSubject<UserProfile | null>
    
    // Implement all interface methods with in-memory storage
}
```

## Platform Implementation Guidelines

### iOS/Swift
- Use `Combine` framework for reactive streams (`CurrentValueSubject`)
- Implement `@Published` properties for SwiftUI integration
- Handle async/await with proper error propagation
- Use `ListenerRegistration` for Firestore listeners

### Android/Kotlin
- Use `Flow` and `StateFlow` for reactive streams
- Implement with coroutines and `suspend` functions
- Use `Hilt` for dependency injection
- Handle lifecycle-aware subscriptions with `viewModelScope`

### Web/JavaScript
- Use `RxJS` or similar reactive library
- Implement with `async/await` and Promises
- Handle component lifecycle and cleanup
- Use `onSnapshot` for Firestore real-time updates

### Flutter/Dart
- Use `Stream` and `StreamBuilder` for reactive UI
- Implement with `async/await` and proper error handling
- Handle widget lifecycle and disposal
- Use `StreamSubscription` for listener management

## Security Considerations

### Data Access Control
- All queries are scoped to authenticated user (`userId` filtering)
- Profile data is private by default (`isPublic` flag for specimens)
- Image storage uses user-scoped paths
- Tags are user-specific to prevent data leakage

### Input Validation
- Validate all user inputs before Firestore operations
- Sanitize file uploads and check file types
- Validate GPS coordinates and measurement values
- Enforce business rules (required fields, value ranges)

This architecture ensures scalable, maintainable, and performant data management while providing real-time synchronization across all clients and platforms.