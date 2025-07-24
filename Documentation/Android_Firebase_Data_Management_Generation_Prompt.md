# Android Firebase Data Management Generation Prompt

## Objective
Create a comprehensive Android implementation of the FossilVault Firebase data management system using modern Android development practices, Kotlin coroutines, and Jetpack libraries. Follow the platform-agnostic specifications from the Data Models Guide and Firebase Data Management Guide.

## Architecture Requirements

### 1. Project Structure
```
data/
├── models/
│   ├── Specimen.kt
│   ├── Tag.kt
│   ├── UserProfile.kt
│   ├── StoredImage.kt
│   ├── AppSettings.kt
│   └── enums/
│       ├── Period.kt
│       ├── SizeUnit.kt
│       ├── Currency.kt
│       └── FossilElement.kt
├── repository/
│   ├── interfaces/
│   │   ├── DatabaseManaging.kt
│   │   └── ImageStoring.kt
│   └── impl/
│       ├── FirestoreDataRepository.kt
│       └── FirebaseStorageRepository.kt
├── local/
│   ├── dao/
│   │   ├── SpecimenDao.kt
│   │   ├── TagDao.kt
│   │   └── UserProfileDao.kt
│   └── database/
│       └── FossilVaultDatabase.kt
└── di/
    ├── DatabaseModule.kt
    ├── StorageModule.kt
    └── RepositoryModule.kt
```

### 2. Dependencies
Add these dependencies to your `build.gradle.kts`:

```kotlin
dependencies {
    // Firebase
    implementation("com.google.firebase:firebase-firestore-ktx:24.9.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    
    // Coroutines & Flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
    
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Room (local caching)
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    kapt("androidx.room:room-compiler:2.6.0")
    
    // Image handling
    implementation("androidx.core:core-ktx:1.12.0")
    
    // Date handling
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
}
```

## Data Models Implementation

### 1. Core Models with Kotlinx Serialization

#### Specimen Model
```kotlin
@Serializable
data class Specimen(
    val id: String,
    val userId: String,
    val species: String,
    val period: Period,
    val element: FossilElement,
    
    // Location Information
    val location: String? = null,
    val formation: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    
    // Physical Measurements
    val width: Double? = null,
    val height: Double? = null,
    val length: Double? = null,
    val unit: SizeUnit = SizeUnit.MM,
    
    // Dates (use kotlinx-datetime)
    @Serializable(with = InstantSerializer::class)
    val collectionDate: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val acquisitionDate: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val creationDate: Instant = Clock.System.now(),
    
    // Additional Metadata
    val inventoryId: String? = null,
    val notes: String? = null,
    
    // Media
    val imageUrls: List<StoredImage> = emptyList(),
    
    // Organization
    val isFavorite: Boolean = false,
    val tagNames: List<String> = emptyList(),
    val isPublic: Boolean = false,
    
    // Valuation
    val pricePaid: Double? = null,
    val pricePaidCurrency: Currency? = null,
    val estimatedValue: Double? = null,
    val estimatedValueCurrency: Currency? = null
) {
    // Validation
    fun validate(): Result<Unit> {
        return when {
            id.isBlank() -> Result.failure(ValidationException("ID cannot be blank"))
            userId.isBlank() -> Result.failure(ValidationException("User ID cannot be blank"))
            species.isBlank() -> Result.failure(ValidationException("Species cannot be blank"))
            latitude != null && (latitude < -90 || latitude > 90) -> 
                Result.failure(ValidationException("Invalid latitude"))
            longitude != null && (longitude < -180 || longitude > 180) -> 
                Result.failure(ValidationException("Invalid longitude"))
            width != null && width < 0 -> 
                Result.failure(ValidationException("Width cannot be negative"))
            height != null && height < 0 -> 
                Result.failure(ValidationException("Height cannot be negative"))
            length != null && length < 0 -> 
                Result.failure(ValidationException("Length cannot be negative"))
            pricePaid != null && pricePaid < 0 -> 
                Result.failure(ValidationException("Price paid cannot be negative"))
            estimatedValue != null && estimatedValue < 0 -> 
                Result.failure(ValidationException("Estimated value cannot be negative"))
            else -> Result.success(Unit)
        }
    }
    
    // CSV Export
    fun toCsvRow(): String {
        val formatter = DateTimeFormatter.ISO_INSTANT
        return csvEscape(
            listOf(
                id, species, period.displayName, element.displayString,
                location ?: "", formation ?: "",
                latitude?.toString() ?: "", longitude?.toString() ?: "",
                width?.toString() ?: "", height?.toString() ?: "", 
                length?.toString() ?: "", unit.name,
                collectionDate?.let(formatter::format) ?: "",
                acquisitionDate?.let(formatter::format) ?: "",
                inventoryId ?: "", notes ?: "",
                formatter.format(creationDate),
                pricePaid?.toString() ?: "",
                pricePaidCurrency?.name ?: "",
                estimatedValue?.toString() ?: "",
                estimatedValueCurrency?.name ?: ""
            )
        ).joinToString(",")
    }
    
    private fun csvEscape(values: List<String>): List<String> {
        return values.map { value ->
            when {
                value.contains(",") || value.contains("\"") || value.contains("\n") ->
                    "\"${value.replace("\"", "\"\"")}\""
                else -> value
            }
        }
    }
    
    companion object {
        const val CSV_HEADER = "Identifier,Species,Period,Element,Location,Formation," +
                "Latitude,Longitude,Width,Height,Length,Unit,Collection Date," +
                "Acquisition Date,Inventory ID,Notes,Creation Date,Price Paid," +
                "Price Paid Currency,Estimated Value,Estimated Value Currency"
    }
}
```

#### Tag Model
```kotlin
@Serializable
data class Tag(
    val userId: String? = null,
    val name: String
) {
    val id: String get() = name
    
    init {
        require(name.isNotBlank()) { "Tag name cannot be blank" }
    }
    
    // Normalize name to lowercase
    fun normalized(): Tag = copy(name = name.lowercase())
}
```

#### UserProfile Model
```kotlin
@Serializable
data class UserProfile(
    val userId: String,
    val email: String,
    val fullName: String? = null,
    val username: String? = null,
    val location: String? = null,
    val bio: String? = null,
    val isPublic: Boolean = false,
    val picture: StoredImage? = null,
    val settings: AppSettings = AppSettings()
) {
    fun validate(): Result<Unit> {
        return when {
            userId.isBlank() -> Result.failure(ValidationException("User ID cannot be blank"))
            email.isBlank() -> Result.failure(ValidationException("Email cannot be blank"))
            !isValidEmail(email) -> Result.failure(ValidationException("Invalid email format"))
            else -> Result.success(Unit)
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
```

### 2. Supporting Models

#### StoredImage
```kotlin
@Serializable
data class StoredImage(
    val url: String,
    val path: String
) {
    constructor(url: String) : this(url, url)
    
    val isLocal: Boolean get() = url.startsWith("file://")
}
```

#### AppSettings
```kotlin
@Serializable
data class AppSettings(
    val unit: SizeUnit = SizeUnit.MM,
    val divideCarboniferous: Boolean = false,
    val defaultCurrency: Currency = Currency.getDeviceDefault()
)
```

### 3. Enum Implementations

#### Period Enum
```kotlin
@Serializable
enum class Period(val displayName: String) {
    @SerialName("precambrian") PRECAMBRIAN("Precambrian"),
    @SerialName("cambrian") CAMBRIAN("Cambrian"),
    @SerialName("ordovician") ORDOVICIAN("Ordovician"),
    @SerialName("silurian") SILURIAN("Silurian"),
    @SerialName("devonian") DEVONIAN("Devonian"),
    @SerialName("carboniferous") CARBONIFEROUS("Carboniferous"),
    @SerialName("mississippian") MISSISSIPPIAN("Mississippian"),
    @SerialName("pennsylvanian") PENNSYLVANIAN("Pennsylvanian"),
    @SerialName("permian") PERMIAN("Permian"),
    @SerialName("triassic") TRIASSIC("Triassic"),
    @SerialName("jurassic") JURASSIC("Jurassic"),
    @SerialName("cretaceous") CRETACEOUS("Cretaceous"),
    @SerialName("paleocene") PALEOCENE("Paleogene"),
    @SerialName("neogene") NEOGENE("Neogene"),
    @SerialName("quaternary") QUATERNARY("Quaternary"),
    @SerialName("unknown") UNKNOWN("Unknown");
    
    companion object {
        fun getAllCases(divideCarboniferous: Boolean): List<Period> {
            val base = listOf(
                PRECAMBRIAN, CAMBRIAN, ORDOVICIAN, SILURIAN, DEVONIAN
            )
            val carboniferous = if (divideCarboniferous) {
                listOf(MISSISSIPPIAN, PENNSYLVANIAN)
            } else {
                listOf(CARBONIFEROUS)
            }
            val remaining = listOf(
                PERMIAN, TRIASSIC, JURASSIC, CRETACEOUS, 
                PALEOCENE, NEOGENE, QUATERNARY, UNKNOWN
            )
            return base + carboniferous + remaining
        }
    }
}
```

#### Currency Enum with Multi-Currency Support
```kotlin
@Serializable
enum class Currency(
    val currencyCode: String,
    val symbol: String,
    val displayName: String
) {
    @SerialName("USD") USD("USD", "$", "US Dollar"),
    @SerialName("EUR") EUR("EUR", "€", "Euro"),
    @SerialName("GBP") GBP("GBP", "£", "British Pound"),
    @SerialName("JPY") JPY("JPY", "¥", "Japanese Yen"),
    @SerialName("CAD") CAD("CAD", "$", "Canadian Dollar"),
    @SerialName("AUD") AUD("AUD", "$", "Australian Dollar"),
    // ... add all other currencies from the spec
    
    companion object {
        fun getDeviceDefault(): Currency {
            val locale = Locale.getDefault()
            val currencyCode = try {
                java.util.Currency.getInstance(locale).currencyCode
            } catch (e: Exception) {
                "USD"
            }
            
            return values().find { it.currencyCode == currencyCode } ?: USD
        }
        
        fun formatAmount(amount: Double, currency: Currency): String {
            val formatter = NumberFormat.getCurrencyInstance()
            formatter.currency = java.util.Currency.getInstance(currency.currencyCode)
            return formatter.format(amount)
        }
    }
}

// Multi-currency calculations
data class MultiCurrencyValue(
    val totals: Map<Currency, Double>,
    val primaryCurrency: Currency,
    val specimenCount: Int
) {
    val primaryTotal: Double get() = totals[primaryCurrency] ?: 0.0
    val hasMultipleCurrencies: Boolean get() = totals.size > 1
    val currencyCount: Int get() = totals.size
    val isEmpty: Boolean get() = totals.isEmpty() || totals.values.all { it == 0.0 }
    
    val sortedCurrencies: List<Pair<Currency, Double>> 
        get() = totals.toList().sortedByDescending { it.second }
    
    companion object {
        fun calculateTotalSpent(
            specimens: List<Specimen>,
            primaryCurrency: Currency
        ): MultiCurrencyValue {
            val specimensWithPrice = specimens.filter { it.pricePaid != null }
            val currencyTotals = mutableMapOf<Currency, Double>()
            
            specimensWithPrice.forEach { specimen ->
                val price = specimen.pricePaid ?: return@forEach
                val currency = specimen.pricePaidCurrency ?: primaryCurrency
                currencyTotals[currency] = currencyTotals.getOrDefault(currency, 0.0) + price
            }
            
            return MultiCurrencyValue(
                totals = currencyTotals,
                primaryCurrency = primaryCurrency,
                specimenCount = specimensWithPrice.size
            )
        }
    }
}
```

## Repository Implementation

### 1. Interface Definitions

#### DatabaseManaging Interface
```kotlin
interface DatabaseManaging {
    // Reactive data streams
    val specimens: Flow<List<Specimen>>
    val tags: Flow<List<Tag>>
    val profile: Flow<UserProfile?>
    
    // Specimen operations
    suspend fun save(specimen: Specimen)
    suspend fun update(specimen: Specimen)
    suspend fun getSpecimen(identifier: String): Specimen?
    suspend fun getAllSpecimens(): List<Specimen>
    suspend fun deleteSpecimen(identifier: String)
    
    // Tag operations
    suspend fun save(tag: Tag)
    
    // Profile operations
    suspend fun updateProfile(profile: UserProfile, imageUrl: StoredImage? = null)
    
    // Utility
    suspend fun clearAllData()
}
```

#### ImageStoring Interface
```kotlin
interface ImageStoring {
    suspend fun uploadImage(imageData: ByteArray, folder: String): StoredImage
    suspend fun uploadImages(imagesData: List<ByteArray>, folder: String): List<StoredImage>
    suspend fun deleteImage(image: StoredImage)
    suspend fun deleteImages(images: List<StoredImage>)
}
```

### 2. Firebase Implementation

#### FirestoreDataRepository
```kotlin
@Singleton
class FirestoreDataRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val imageStoring: ImageStoring,
    private val authManager: AuthenticationManager,
    @ApplicationContext private val context: Context
) : DatabaseManaging {
    
    private val _specimens = MutableStateFlow<List<Specimen>>(emptyList())
    override val specimens: Flow<List<Specimen>> = _specimens.asStateFlow()
    
    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    override val tags: Flow<List<Tag>> = _tags.asStateFlow()
    
    private val _profile = MutableStateFlow<UserProfile?>(null)
    override val profile: Flow<UserProfile?> = _profile.asStateFlow()
    
    private var specimenListener: ListenerRegistration? = null
    private var tagListener: ListenerRegistration? = null
    private var profileListener: ListenerRegistration? = null
    
    init {
        setupAuthStateListener()
    }
    
    private fun setupAuthStateListener() {
        authManager.isAuthenticated
            .onEach { isAuthenticated ->
                if (isAuthenticated) {
                    setupFirestoreListeners()
                } else {
                    removeListeners()
                    clearLocalData()
                }
            }
            .launchIn(GlobalScope) // Use appropriate scope
    }
    
    private suspend fun getUserId(): String {
        return authManager.getCurrentUserId() 
            ?: throw IllegalStateException("No user logged in")
    }
    
    private fun setupFirestoreListeners() {
        viewModelScope.launch {
            try {
                val userId = getUserId()
                
                // Specimens listener
                specimenListener = firestore.collection("specimens")
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("FirestoreRepo", "Specimens listener error", error)
                            return@addSnapshotListener
                        }
                        
                        snapshot?.let { querySnapshot ->
                            val specimens = querySnapshot.documents.mapNotNull { doc ->
                                try {
                                    doc.toObject(Specimen::class.java)
                                } catch (e: Exception) {
                                    Log.e("FirestoreRepo", "Error parsing specimen", e)
                                    null
                                }
                            }
                            _specimens.value = specimens
                        }
                    }
                
                // Tags listener
                tagListener = firestore.collection("tags")
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("FirestoreRepo", "Tags listener error", error)
                            return@addSnapshotListener
                        }
                        
                        snapshot?.let { querySnapshot ->
                            val tags = querySnapshot.documents.mapNotNull { doc ->
                                try {
                                    doc.toObject(Tag::class.java)
                                } catch (e: Exception) {
                                    Log.e("FirestoreRepo", "Error parsing tag", e)
                                    null
                                }
                            }
                            _tags.value = tags
                        }
                    }
                
                // Profile listener
                profileListener = firestore.collection("users")
                    .whereEqualTo("userId", userId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Log.e("FirestoreRepo", "Profile listener error", error)
                            return@addSnapshotListener
                        }
                        
                        snapshot?.let { querySnapshot ->
                            val profiles = querySnapshot.documents.mapNotNull { doc ->
                                try {
                                    doc.toObject(UserProfile::class.java)
                                } catch (e: Exception) {
                                    Log.e("FirestoreRepo", "Error parsing profile", e)
                                    null
                                }
                            }
                            _profile.value = profiles.firstOrNull()
                        }
                    }
            } catch (e: Exception) {
                Log.e("FirestoreRepo", "Error setting up listeners", e)
            }
        }
    }
    
    override suspend fun save(specimen: Specimen): Unit = withContext(Dispatchers.IO) {
        specimen.validate().getOrThrow()
        
        var updatedSpecimen = specimen
        
        // Handle local images
        val localImages = specimen.imageUrls.filter { it.isLocal }
        if (localImages.isNotEmpty()) {
            val imageDataList = localImages.mapNotNull { image ->
                loadImageData(image.url)
            }
            
            val remoteImages = imageStoring.uploadImages(imageDataList, "specimen")
            val existingRemoteUrls = specimen.imageUrls.filter { !it.isLocal }
            updatedSpecimen = specimen.copy(imageUrls = existingRemoteUrls + remoteImages)
        }
        
        firestore.collection("specimens")
            .document(updatedSpecimen.id)
            .set(updatedSpecimen)
            .await()
    }
    
    override suspend fun update(specimen: Specimen): Unit = withContext(Dispatchers.IO) {
        val existingSpecimen = getSpecimen(specimen.id)
        
        if (existingSpecimen == null) {
            save(specimen)
            return@withContext
        }
        
        // Find removed images
        val imagesToRemove = existingSpecimen.imageUrls.filter { oldImage ->
            !specimen.imageUrls.contains(oldImage) && !oldImage.isLocal
        }
        
        // Delete removed images
        if (imagesToRemove.isNotEmpty()) {
            imageStoring.deleteImages(imagesToRemove)
        }
        
        save(specimen)
    }
    
    override suspend fun deleteSpecimen(identifier: String): Unit = withContext(Dispatchers.IO) {
        val specimen = getSpecimen(identifier)
        
        // Delete associated images
        specimen?.let {
            if (it.imageUrls.isNotEmpty()) {
                imageStoring.deleteImages(it.imageUrls)
            }
        }
        
        firestore.collection("specimens")
            .document(identifier)
            .delete()
            .await()
    }
    
    override suspend fun save(tag: Tag): Unit = withContext(Dispatchers.IO) {
        val userId = getUserId()
        val normalizedTag = tag.normalized().copy(userId = userId)
        val documentId = "${userId}_${normalizedTag.name}"
        
        firestore.collection("tags")
            .document(documentId)
            .set(normalizedTag)
            .await()
    }
    
    private fun loadImageData(fileUrl: String): ByteArray? {
        return try {
            val uri = Uri.parse(fileUrl)
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error loading image data", e)
            null
        }
    }
    
    private fun removeListeners() {
        specimenListener?.remove()
        tagListener?.remove()
        profileListener?.remove()
        
        specimenListener = null
        tagListener = null
        profileListener = null
    }
    
    private fun clearLocalData() {
        _specimens.value = emptyList()
        _tags.value = emptyList()
        _profile.value = null
    }
}
```

#### FirebaseStorageRepository
```kotlin
@Singleton
class FirebaseStorageRepository @Inject constructor(
    private val storage: FirebaseStorage,
    private val authManager: AuthenticationManager
) : ImageStoring {
    
    private suspend fun getUserId(): String {
        return authManager.getCurrentUserId() 
            ?: throw IllegalStateException("No user logged in")
    }
    
    private fun getImageExtension(data: ByteArray): String {
        val header = data.take(3).joinToString("") { 
            "%02x".format(it) 
        }
        
        return when (header) {
            "ffd8ff" -> "jpg"
            "89504e" -> "png"
            "474946" -> "gif"
            else -> "jpg"
        }
    }
    
    override suspend fun uploadImage(imageData: ByteArray, folder: String): StoredImage = 
        withContext(Dispatchers.IO) {
            val userId = getUserId()
            val extension = getImageExtension(imageData)
            val imageName = "${UUID.randomUUID()}.$extension"
            val path = "$folder/$userId/$imageName"
            
            val storageRef = storage.reference.child(path)
            val uploadTask = storageRef.putBytes(imageData).await()
            val downloadUrl = storageRef.downloadUrl.await()
            
            StoredImage(url = downloadUrl.toString(), path = path)
        }
    
    override suspend fun uploadImages(
        imagesData: List<ByteArray>, 
        folder: String
    ): List<StoredImage> = withContext(Dispatchers.IO) {
        imagesData.map { imageData ->
            async { uploadImage(imageData, folder) }
        }.awaitAll()
    }
    
    override suspend fun deleteImage(image: StoredImage): Unit = withContext(Dispatchers.IO) {
        storage.reference.child(image.path).delete().await()
    }
    
    override suspend fun deleteImages(images: List<StoredImage>): Unit = withContext(Dispatchers.IO) {
        images.map { image ->
            async { deleteImage(image) }
        }.awaitAll()
    }
}
```

## Dependency Injection Setup

### Hilt Modules
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    abstract fun bindDatabaseManaging(
        firestoreDataRepository: FirestoreDataRepository
    ): DatabaseManaging
    
    @Binds
    abstract fun bindImageStoring(
        firebaseStorageRepository: FirebaseStorageRepository
    ): ImageStoring
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}
```

## Testing Implementation

### Unit Tests
```kotlin
@RunWith(MockitoJUnitRunner::class)
class FirestoreDataRepositoryTest {
    
    @Mock private lateinit var firestore: FirebaseFirestore
    @Mock private lateinit var imageStoring: ImageStoring
    @Mock private lateinit var authManager: AuthenticationManager
    @Mock private lateinit var context: Context
    
    private lateinit var repository: FirestoreDataRepository
    
    @Before
    fun setup() {
        repository = FirestoreDataRepository(firestore, imageStoring, authManager, context)
    }
    
    @Test
    fun `save specimen with valid data succeeds`() = runTest {
        // Given
        val specimen = createValidSpecimen()
        val mockCollection = mock<CollectionReference>()
        val mockDocument = mock<DocumentReference>()
        val mockTask = mock<Task<Void>>()
        
        whenever(authManager.getCurrentUserId()).thenReturn("user123")
        whenever(firestore.collection("specimens")).thenReturn(mockCollection)
        whenever(mockCollection.document(specimen.id)).thenReturn(mockDocument)
        whenever(mockDocument.set(specimen)).thenReturn(mockTask)
        whenever(mockTask.await()).thenReturn(null)
        
        // When
        repository.save(specimen)
        
        // Then
        verify(mockDocument).set(specimen)
    }
    
    private fun createValidSpecimen(): Specimen {
        return Specimen(
            id = "spec123",
            userId = "user123",
            species = "T. Rex",
            period = Period.CRETACEOUS,
            element = FossilElement.TOOTH
        )
    }
}
```

## Error Handling & Validation

### Custom Exceptions
```kotlin
sealed class DataException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class ValidationException(message: String) : DataException(message)
    class NetworkException(message: String, cause: Throwable?) : DataException(message, cause)
    class StorageException(message: String, cause: Throwable?) : DataException(message, cause)
    class AuthenticationException(message: String) : DataException(message)
}
```

## Performance Optimizations

### Room Integration for Offline Caching
```kotlin
@Entity(tableName = "specimens")
data class SpecimenEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val species: String,
    // ... other fields
    val lastModified: Long = System.currentTimeMillis()
)

@Dao
interface SpecimenDao {
    @Query("SELECT * FROM specimens WHERE userId = :userId")
    fun getAllSpecimens(userId: String): Flow<List<SpecimenEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecimens(specimens: List<SpecimenEntity>)
    
    @Delete
    suspend fun deleteSpecimen(specimen: SpecimenEntity)
}
```

## Usage Examples

### ViewModel Integration
```kotlin
@HiltViewModel
class SpecimenListViewModel @Inject constructor(
    private val repository: DatabaseManaging
) : ViewModel() {
    
    val specimens = repository.specimens
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun saveSpecimen(specimen: Specimen) {
        viewModelScope.launch {
            try {
                repository.save(specimen)
            } catch (e: DataException) {
                // Handle error
            }
        }
    }
}
```

This implementation provides a complete Android data management solution that matches the iOS functionality while following modern Android development practices and leveraging Kotlin's powerful features for type safety and coroutines for asynchronous operations.