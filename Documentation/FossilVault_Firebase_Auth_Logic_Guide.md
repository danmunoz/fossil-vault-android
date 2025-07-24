# FossilVault Firebase Authentication Logic - Platform-Agnostic Guide

## Architecture Overview

The FossilVault authentication system implements a clean, protocol-based architecture that separates authentication concerns from UI logic. This design enables easy testing, platform portability, and provider swapping.

## Core Components

### 1. Authentication States

The system defines four distinct authentication states:

```typescript
enum AuthenticationState {
    unauthenticated    // No user signed in
    authenticating     // Authentication in progress
    authenticated      // Fully authenticated user
    localUser         // Anonymous/guest user mode
}
```

### 2. User Profile Model

```typescript
interface UserProfile {
    userId: string
    email: string
    fullName?: string
    username?: string
    location?: string
    bio?: string
    isPublic: boolean
    picture?: StoredImage
    settings: AppSettings
}
```

### 3. Authentication Protocol/Interface

The `Authenticable` protocol defines the contract for authentication providers:

```typescript
interface Authenticable {
    // State management
    profileSubject: Observable<UserProfile | null>
    authenticationState: Observable<AuthenticationState>
    
    // Core authentication methods
    signInWith(email: string, password: string): Promise<void>
    signUpWith(email: string, password: string): Promise<void>
    signOut(): void
    deleteAccount(): Promise<void>
    
    // Anonymous/local user support
    signInAsLocalUser(): Promise<void>
    isLocalUser(): boolean
    
    // Profile management
    updateProfile(id: string, email: string, username: string, location: string): void
}
```

## Firebase Implementation Details

### 1. Provider Configuration

```typescript
class FBAuthentication implements Authenticable {
    private user: FirebaseUser | null
    private authStateHandler: AuthStateListener
    
    constructor(serverIP?: string) {
        // Configure Firebase Auth emulator for development
        if (serverIP) {
            Auth.useEmulator(serverIP, 9099)
        }
        this.setupAuthStateHandler()
    }
}
```

### 2. State Management

The Firebase provider uses reactive streams to manage authentication state:

```typescript
private setupAuthStateHandler() {
    this.authStateHandler = Auth.onAuthStateChanged((user) => {
        this.user = user
        
        if (user) {
            // User is signed in
            const profile = new UserProfile({
                userId: user.uid,
                email: user.email || "",
                username: user.displayName
            })
            
            this.profileSubject.next(profile)
            this.authenticationState.next(
                user.isAnonymous ? AuthenticationState.localUser : AuthenticationState.authenticated
            )
        } else {
            // User is signed out
            this.profileSubject.next(null)
            this.authenticationState.next(AuthenticationState.unauthenticated)
        }
    })
}
```

### 3. Authentication Methods

#### Sign In
```typescript
async signInWith(email: string, password: string): Promise<void> {
    try {
        const result = await Auth.signInWithEmailAndPassword(email, password)
        const user = result.user
        const profile = new UserProfile({
            userId: user.uid,
            email: user.email || "",
            username: user.displayName
        })
        
        this.profileSubject.next(profile)
        this.authenticationState.next(AuthenticationState.authenticated)
    } catch (error) {
        this.authenticationState.next(AuthenticationState.unauthenticated)
        throw error
    }
}
```

#### Sign Up
```typescript
async signUpWith(email: string, password: string): Promise<void> {
    try {
        const result = AuthDataResult = this.authenticationState.value === AuthenticationState.localUser
            ? await this.signUpFromAnonymous(email, password)
            : await Auth.createUserWithEmailAndPassword(email, password)
            
        const user = result.user
        const profile = new UserProfile({
            userId: user.uid,
            email: user.email || "",
            username: user.displayName
        })
        
        this.profileSubject.next(profile)
        this.authenticationState.next(AuthenticationState.authenticated)
    } catch (error) {
        if (this.authenticationState.value !== AuthenticationState.localUser) {
            this.authenticationState.next(AuthenticationState.unauthenticated)
        }
        throw error
    }
}
```

#### Anonymous User Conversion
```typescript
async signUpFromAnonymous(email: string, password: string): Promise<AuthDataResult> {
    if (!this.user) {
        throw new Error("Unable to convert anonymous user")
    }
    
    const credential = EmailAuthProvider.credential(email, password)
    return await this.user.linkWithCredential(credential)
}
```

#### Anonymous Sign In
```typescript
async signInAsLocalUser(): Promise<void> {
    try {
        const result = await Auth.signInAnonymously()
        const user = result.user
        const profile = new UserProfile({
            userId: user.uid,
            email: user.email || "",
            username: user.displayName
        })
        
        this.profileSubject.next(profile)
        this.authenticationState.next(AuthenticationState.localUser)
    } catch (error) {
        this.authenticationState.next(AuthenticationState.unauthenticated)
        throw error
    }
}
```

### 4. Profile Management

```typescript
updateProfile(id: string, email: string, username: string, location: string): void {
    const currentUser = Auth.currentUser
    if (!currentUser) return
    
    // Update Firebase user profile
    currentUser.updateProfile({
        displayName: username
    }).then(() => {
        // Update local profile after successful Firebase update
        const profile = new UserProfile({
            userId: id,
            email: email,
            username: username,
            location: location
        })
        this.profileSubject.next(profile)
    }).catch((error) => {
        console.error("Error updating Firebase profile:", error)
    })
}
```

### 5. Sign Out & Account Deletion

```typescript
signOut(): void {
    Auth.signOut()
    // Auth state listener will handle state updates automatically
}

async deleteAccount(): Promise<void> {
    await Auth.currentUser?.delete()
    // Auth state listener will handle state updates automatically
}
```

## Manager Layer

The `AuthenticationManager` provides a reactive interface for UI components:

```typescript
class AuthenticationManager {
    public isAuthenticated: Observable<boolean>
    public profile: Observable<UserProfile | null>
    public authenticationState: Observable<AuthenticationState>
    
    private authenticationProvider: Authenticable
    
    constructor(authenticationProvider: Authenticable) {
        this.authenticationProvider = authenticationProvider
        
        // Map provider state to UI-friendly observables
        this.authenticationState = authenticationProvider.authenticationState
        this.profile = authenticationProvider.profileSubject
        this.isAuthenticated = authenticationProvider.authenticationState.map(
            state => state === AuthenticationState.localUser || state === AuthenticationState.authenticated
        )
    }
    
    // Delegate methods to provider
    async signIn(email: string, password: string): Promise<void> {
        return this.authenticationProvider.signInWith(email, password)
    }
    
    async signUp(email: string, password: string): Promise<void> {
        return this.authenticationProvider.signUpWith(email, password)
    }
    
    signOut(): void {
        this.authenticationProvider.signOut()
    }
    
    async deleteAccount(): Promise<void> {
        return this.authenticationProvider.deleteAccount()
    }
    
    anonymousSignIn(): void {
        this.authenticationProvider.signInAsLocalUser()
    }
}
```

## Error Handling

### Custom Error Types
```typescript
enum AuthenticationError {
    unableToConvertAnonymous = "Unable to convert anonymous user"
}
```

### Error Propagation
- Firebase errors are caught and re-thrown to maintain error context
- UI components receive localized error messages
- State is properly managed during error scenarios

## Key Features

### 1. Anonymous User Support
- Users can start as anonymous/local users
- Seamless conversion from anonymous to authenticated
- Data preservation during account linking

### 2. Reactive State Management
- All authentication state changes are observable
- UI automatically updates based on authentication state
- Proper cleanup and memory management

### 3. Development Support
- Firebase Auth emulator integration for local development
- Configurable server endpoints
- Mock authentication providers for testing

### 4. Platform Agnostic Design
- Protocol-based architecture enables easy platform porting
- Separation of concerns between authentication logic and UI
- Testable and mockable components

## Implementation Guidelines

### Platform-Specific Considerations

#### iOS/Swift
- Use `@Published` properties for reactive UI updates
- Implement with `CurrentValueSubject` from Combine
- Handle async/await with proper error propagation

#### Android/Kotlin
- Use `StateFlow` or `LiveData` for reactive updates
- Implement with coroutines for async operations
- Handle lifecycle-aware subscriptions

#### Web/JavaScript
- Use RxJS observables or similar reactive library
- Implement with Promises/async-await
- Handle component lifecycle and cleanup

#### Flutter/Dart
- Use `Stream` and `StreamBuilder` for reactive UI
- Implement with async/await and proper error handling
- Handle widget lifecycle and disposal

### Testing Strategy
- Mock the `Authenticable` protocol/interface
- Test authentication flows in isolation
- Verify state transitions and error handling
- Test UI components with mocked authentication states

This architecture ensures consistent authentication behavior across all platforms while maintaining platform-specific optimizations and patterns.