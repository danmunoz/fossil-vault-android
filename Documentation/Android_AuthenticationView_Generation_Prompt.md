# Android Authentication View Generation Prompt

## Objective
Create a complete Android authentication screen for the FossilVault app that matches the iOS implementation in terms of UX, visual design, and functionality. Use the provided UX/Style Guide and Firebase Auth Logic Guide as your primary references for implementation.

## Required Implementation

### 1. UI Framework & Architecture
- **Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM pattern with StateFlow for reactive state management
- **Navigation**: Support for modal presentation (similar to iOS sheet)
- **Dependency Injection**: Hilt for dependency management

### 2. Screen Structure & Layout
Create a `AuthenticationScreen` composable with the following structure:

```kotlin
@Composable
fun AuthenticationScreen(
    onDismiss: () -> Unit,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    // Implementation here
}
```

**Layout Requirements:**
- **Root Container**: `Scaffold` with `TopAppBar` containing close button
- **Main Content**: `LazyColumn` for scrollable content
- **Sections**: Header (logo + titles), Form fields, Action buttons, Mode switch
- **Spacing**: Follow Material Design guidelines while matching iOS spacing ratios
- **Padding**: 24dp horizontal margins, 40dp top padding

### 3. Visual Design Implementation

#### Color Scheme
Define a custom color scheme matching the iOS design:
```kotlin
val FossilVaultColors = lightColorScheme(
    primary = Color(0xFF3B82F6),           // iconBlue
    secondary = Color(0xFF8B5CF6),         // gradientEnd
    surface = Color(0xFFFAF9F6),           // background (cream/off-white)
    onSurface = Color(0xFF111827),         // textPrimary
    onSurfaceVariant = Color(0xFF6B7280),  // textGray
    error = Color(0xFFEF4444),             // validation error
    surfaceVariant = Color(0xFFF3F4F6),    // inputBackground
    outline = Color(0xFFE5E7EB)            // border
)
```

#### Typography
Create custom typography matching iOS specifications:
```kotlin
val FossilVaultTypography = Typography(
    headlineLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),      // Main title
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),       // Subtitle
    labelMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),     // Field labels
    bodyMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal),      // Input text
    labelSmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)       // Small caption
)
```

#### Component Styling
- **Input Fields**: 12dp corner radius, 12dp internal padding, full width
- **Buttons**: Gradient background (blue to purple), 12dp corner radius
- **Cards/Containers**: 16dp corner radius where applicable
- **Shadows**: Subtle elevation matching iOS shadow specifications

### 4. Authentication Modes
Implement enum for authentication modes:
```kotlin
enum class AuthenticationMode(
    val titleRes: Int,
    val subtitleRes: Int,
    val primaryButtonRes: Int,
    val primaryButtonIcon: ImageVector,
    val switchPromptRes: Int,
    val switchActionRes: Int
) {
    LOGIN(
        titleRes = R.string.auth_welcome_back,
        subtitleRes = R.string.auth_signin_subtitle,
        primaryButtonRes = R.string.auth_sign_in,
        primaryButtonIcon = Icons.Filled.ArrowForward,
        switchPromptRes = R.string.auth_no_account,
        switchActionRes = R.string.auth_create_account
    ),
    SIGN_UP(
        titleRes = R.string.auth_get_started,
        subtitleRes = R.string.auth_signup_subtitle,
        primaryButtonRes = R.string.auth_create_account,
        primaryButtonIcon = Icons.Filled.PersonAdd,
        switchPromptRes = R.string.auth_have_account,
        switchActionRes = R.string.auth_sign_in
    )
}
```

### 5. State Management
Implement ViewModel with proper state handling:
```kotlin
@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthenticationUiState())
    val uiState: StateFlow<AuthenticationUiState> = _uiState.asStateFlow()
    
    // Implementation methods
    fun updateEmail(email: String) { /* */ }
    fun updatePassword(password: String) { /* */ }
    fun switchMode() { /* */ }
    fun signIn() { /* */ }
    fun signUp() { /* */ }
}

data class AuthenticationUiState(
    val mode: AuthenticationMode = AuthenticationMode.SIGN_UP,
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isTransitioning: Boolean = false
)
```

### 6. Form Validation
Implement real-time validation matching iOS behavior:
- **Email validation**: Must contain "@" and not be empty
- **Password validation**: Minimum 6 characters
- **Form state**: Enable/disable submit button based on validation
- **Visual feedback**: Error styling for invalid fields

### 7. Password Strength Indicator (Sign Up Mode)
Create a password strength component:
```kotlin
@Composable
fun PasswordStrengthIndicator(
    password: String,
    modifier: Modifier = Modifier
) {
    val strength = remember(password) { evaluatePasswordStrength(password) }
    
    // Animated progress bar with color coding:
    // Weak (25%) - Red, Fair (50%) - Orange, Good (75%) - Yellow, Strong (100%) - Green
}
```

### 8. Animations & Transitions
Implement smooth animations matching iOS behavior:
- **Mode switching**: Animated content transitions with staggered appearance
- **Loading states**: Button loading animation with progress indicator
- **Error states**: Subtle scale animation and error message transitions
- **Password strength**: Animated progress bar updates
- **Form validation**: Smooth error state transitions

### 9. Logo Implementation
Create an animated logo component:
```kotlin
@Composable
fun AnimatedLogo(
    modifier: Modifier = Modifier
) {
    // Orange diamond shape with white diamond center
    // Scale factor: 0.6, Height: 120dp
    // Include rotation/scaling animations
}
```

### 10. Focus Management
Implement proper focus handling:
- **IME Actions**: Email field advances to password on "Next"
- **Keyboard Types**: Email keyboard for email field, secure for password
- **Focus Clearing**: Clear focus during mode transitions
- **Auto-focus**: Focus email field when screen appears

### 11. Firebase Integration
Implement authentication following the Firebase Auth Logic Guide:
```kotlin
@Singleton
class AndroidFBAuthentication @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Authenticable {
    
    override val profileSubject = MutableStateFlow<UserProfile?>(null)
    override val authenticationState = MutableStateFlow<AuthenticationState>(AuthenticationState.UNAUTHENTICATED)
    
    // Implement all Authenticable interface methods
    override suspend fun signInWith(email: String, password: String) { /* */ }
    override suspend fun signUpWith(email: String, password: String) { /* */ }
    // ... other methods
}
```

### 12. Error Handling
Implement comprehensive error handling:
- **Network errors**: Show user-friendly messages
- **Firebase errors**: Map to localized error messages
- **Validation errors**: Real-time field validation feedback
- **Loading states**: Proper loading indicators during async operations

### 13. Accessibility
Ensure full accessibility support:
- **Content descriptions**: All interactive elements have proper descriptions
- **Semantic roles**: Proper roles for form fields and buttons
- **Focus management**: Logical tab order and focus traversal
- **Screen reader support**: Proper announcements for state changes

### 14. String Resources
Create comprehensive string resources:
```xml
<resources>
    <string name="app_name">FossilVault</string>
    <string name="app_tagline">Your Personal Fossil Collection, Anywhere</string>
    <string name="auth_welcome_back">Welcome back!</string>
    <string name="auth_get_started">Let\'s Get You Started</string>
    <string name="auth_signin_subtitle">Sign in to access your collection and continue exploring</string>
    <string name="auth_signup_subtitle">Join FossilVault and start building your fossil collection</string>
    <!-- Add all other required strings -->
</resources>
```

### 15. Testing Requirements
Include comprehensive testing:
- **Unit tests**: ViewModel logic and validation functions
- **Compose tests**: UI component behavior and interactions
- **Integration tests**: Authentication flow end-to-end
- **Screenshot tests**: Visual regression testing

## Implementation Guidelines

### Code Organization
```
authentication/
├── ui/
│   ├── AuthenticationScreen.kt
│   ├── components/
│   │   ├── AnimatedLogo.kt
│   │   ├── PasswordStrengthIndicator.kt
│   │   └── ValidatedTextField.kt
│   └── AuthenticationViewModel.kt
├── domain/
│   ├── AuthenticationManager.kt
│   └── Authenticable.kt
├── data/
│   └── AndroidFBAuthentication.kt
└── di/
    └── AuthenticationModule.kt
```

### Performance Considerations
- **Lazy initialization**: Initialize expensive components lazily
- **Recomposition optimization**: Use `remember` and `derivedStateOf` appropriately
- **Animation performance**: Use hardware-accelerated animations
- **Memory management**: Proper lifecycle handling and resource cleanup

### Platform-Specific Features
- **Biometric authentication**: Support fingerprint/face unlock where available
- **Auto-fill**: Support Android auto-fill framework for credentials
- **Deep linking**: Handle authentication deep links
- **Back navigation**: Proper back stack management

## Expected Deliverables

1. **Complete Authentication Screen**: Fully functional Compose UI matching iOS design
2. **ViewModel Implementation**: Reactive state management with proper error handling
3. **Firebase Integration**: Complete authentication provider implementation
4. **Custom Components**: Reusable components (logo, text fields, indicators)
5. **Theme & Styling**: Custom theme matching FossilVault design system
6. **String Resources**: Complete localization support
7. **Unit Tests**: Comprehensive test coverage
8. **Documentation**: Implementation notes and usage instructions

## Success Criteria

- **Visual Fidelity**: Matches iOS implementation in appearance and spacing
- **Functional Parity**: All authentication flows work identically to iOS
- **Performance**: Smooth animations and responsive interactions
- **Accessibility**: Full accessibility support with proper semantics
- **Code Quality**: Clean, maintainable code following Android best practices
- **Testing**: High test coverage with reliable test suite

Use the referenced UX/Style Guide and Firebase Auth Logic Guide documents as your primary specification sources. Ensure that every visual element, interaction pattern, and authentication flow matches the documented iOS implementation while following Android/Material Design conventions and best practices.