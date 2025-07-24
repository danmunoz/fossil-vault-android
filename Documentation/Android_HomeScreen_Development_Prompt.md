# Android HomeScreen Development Prompt

## Objective
Create a professional, polished Android version of the Fossil Collector HomeScreen that maintains the core functionality and user experience of the iOS version while following native Android design patterns, Material Design principles, and platform conventions.

## Core Requirements

### 1. Architecture & Framework
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material3 design system
- **Architecture**: MVVM with StateFlow/Flow for reactive data streams
- **State Management**: Use Compose's state management (remember, mutableStateOf, StateFlow)
- **Navigation**: Jetpack Navigation Compose
- **Dependency Injection**: Hilt or manual DI container

### 2. Android-Specific Design Adaptations

#### Top App Bar (Replace iOS Navigation Bar)
**Instead of iOS toolbar buttons, use:**
- **Material3 TopAppBar** with:
  - **Leading**: Navigation icon or overflow menu for settings/profile
  - **Title**: "Fossil Collector" or user's collection name
  - **Actions**: 
    - Stats icon (Material Icons: `Icons.Default.Analytics` or `Icons.Default.PieChart`)
    - Add specimen FAB (see below) or action button
- **Elevation**: Follow Material3 elevation guidelines
- **Colors**: Use Material3 color schemes (primary, surface, etc.)

#### Floating Action Button (Replace iOS Add Button)
**Primary action should use FAB pattern:**
- **Position**: Bottom-right corner (standard Android positioning)
- **Icon**: `Icons.Default.Add`
- **Behavior**: 
  - Tap to add new specimen
  - Show subscription limit dialog if exceeded
  - Use Material3 FAB styling with proper elevation and colors

#### Search Implementation
**Use Material3 SearchBar/DockedSearchBar:**
- **Placement**: Below TopAppBar, integrated with filter section
- **Behavior**: Expands to full-screen search when focused (Android pattern)
- **Hint Text**: "Search your collection"
- **Leading Icon**: Search icon
- **Trailing Icon**: Clear text when typing

#### Filter & Sort Controls
**Replace iOS-style controls with Android equivalents:**
- **Sort Dropdown**: Use `ExposedDropdownMenuBox` with Material3 styling
- **View Mode Toggle**: Use `SegmentedButton` or `IconToggleButton` group
- **Filter Chips**: Use Material3 `FilterChip` components in horizontal scrollable row
- **Result Counter**: Display as subtitle text below search bar

### 3. List/Grid Display Adaptations

#### RecyclerView Alternative in Compose
**Use appropriate Compose components:**
- **Grid Mode**: `LazyVerticalGrid` with `GridCells.Fixed(2)` or `GridCells.Adaptive(minSize)`
- **List Mode**: `LazyColumn` with custom item composables
- **Spacing**: Use Material3 spacing tokens (8dp, 16dp, 24dp)

#### Card Design (Android Material Cards)
**Replace iOS cards with Material3 Cards:**
- **Component**: `Card` with `CardDefaults.cardElevation()`
- **Image Display**: Use `AsyncImage` (Coil library) with proper aspect ratios
- **Content Layout**: Column layout with Material3 typography scale
- **Species Name**: Material3 `MaterialTheme.typography.titleMedium`
- **Metadata**: Material3 `MaterialTheme.typography.bodySmall`
- **Location Info**: Use Material Icons for location (`Icons.Default.LocationOn`)
- **Favorite Indicator**: Material Icons heart (`Icons.Default.Favorite`)

#### Period Tags
**Use Material3 design:**
- **Component**: `AssistChip` or custom chip with Material3 colors
- **Typography**: `MaterialTheme.typography.labelSmall`
- **Colors**: Use Material3 color roles (surface, primary, etc.)

### 4. Context Menu Adaptation (Critical Android Pattern Change)

#### Replace iOS Long-Press Context Menu
**iOS uses long-press context menus, but Android should use:**

**Option A - Bottom Sheet (Recommended):**
- **Trigger**: Long press on card shows Material3 `ModalBottomSheet`
- **Content**: Action list with Material Icons and text
- **Actions**: View, Edit, Share, Delete with appropriate icons
- **Styling**: Material3 bottom sheet with proper elevation and corners

**Option B - Card Action Overflow:**
- **Add**: Three-dot overflow menu (`Icons.Default.MoreVert`) to each card
- **Behavior**: Shows `DropdownMenu` with actions
- **Placement**: Top-right corner of each card

**Option C - Selection Mode (Most Android-native):**
- **Trigger**: Long press enters selection mode
- **Visual**: Selected cards show checkmarks, action bar appears
- **Actions**: Top action bar with edit, share, delete icons
- **Multiple Selection**: Allow selecting multiple items for batch operations

### 5. Modal/Dialog Adaptations

#### Replace iOS Sheets with Android Patterns
**Transform iOS modal presentations:**

**Settings/Profile Screen:**
- Use full-screen activity/destination or `ModalBottomSheet` for simple settings
- Follow Android settings patterns with proper back navigation

**Limit Reached Dialog:**
- Use Material3 `AlertDialog` with appropriate actions
- Include upgrade call-to-action with Material3 button styling

**Add/Edit Specimen:**
- Full-screen destination with proper up navigation
- Use Material3 text fields, dropdowns, and form components

### 6. Android-Specific Enhancements

#### Material3 Theming
```kotlin
// Implement proper Material3 theme
@Composable
fun FossilCollectorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme(
            primary = FossilPrimary,
            secondary = FossilSecondary,
            // ... other colors
        )
        else -> lightColorScheme(
            primary = FossilPrimary,
            secondary = FossilSecondary,
            // ... other colors
        )
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = FossilTypography,
        content = content
    )
}
```

#### Edge-to-Edge Display
- Support Android edge-to-edge display
- Handle system bar insets properly
- Use `WindowCompat.setDecorFitsSystemWindows(window, false)`

#### Adaptive Layout
- Support different screen sizes (phones, tablets, foldables)
- Use `WindowSizeClass` for responsive design
- Consider navigation rail for large screens

#### State Preservation
- Handle configuration changes properly
- Save/restore search state, filter selections, scroll position
- Use `rememberSaveable` for critical state

### 7. Implementation Structure

#### Data Models
```kotlin
data class Specimen(
    val id: String,
    val species: String,
    val period: GeologicalPeriod,
    val location: String?,
    val imageUrls: List<String>,
    val isFavorite: Boolean,
    val creationDate: Instant,
    val element: SpecimenElement
)

enum class SortOption {
    RECENT, OLDEST, NAME_A_Z, NAME_Z_A
}

enum class DisplayMode {
    GRID, LIST
}
```

#### ViewModel Structure
```kotlin
class HomeViewModel : ViewModel() {
    private val _specimens = MutableStateFlow<List<Specimen>>(emptyList())
    val specimens = _specimens.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    private val _selectedPeriod = MutableStateFlow<GeologicalPeriod?>(null)
    val selectedPeriod = _selectedPeriod.asStateFlow()
    
    private val _sortOption = MutableStateFlow(SortOption.RECENT)
    val sortOption = _sortOption.asStateFlow()
    
    private val _displayMode = MutableStateFlow(DisplayMode.GRID)
    val displayMode = _displayMode.asStateFlow()
    
    val filteredSpecimens = combine(
        specimens, searchQuery, selectedPeriod, sortOption
    ) { specimens, query, period, sort ->
        // Filtering and sorting logic
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
```

#### Main Composable Structure
```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSpecimen: (String) -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val specimens by viewModel.filteredSpecimens.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val displayMode by viewModel.displayMode.collectAsState()
    
    Scaffold(
        topBar = { HomeTopBar(/*...*/) },
        floatingActionButton = { AddSpecimenFAB(/*...*/) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchAndFilterSection(/*...*/)
            
            when (displayMode) {
                DisplayMode.GRID -> SpecimenGrid(specimens, /*...*/)
                DisplayMode.LIST -> SpecimenList(specimens, /*...*/)
            }
        }
    }
}
```

### 8. Key Android Best Practices to Follow

#### Performance
- Use `LazyColumn`/`LazyVerticalGrid` for large lists
- Implement proper image loading with Coil
- Use `derivedStateOf` for expensive calculations
- Handle configuration changes efficiently

#### Accessibility
- Provide content descriptions for all interactive elements
- Support TalkBack properly
- Ensure minimum touch target sizes (48dp)
- Use semantic properties for screen readers

#### User Experience
- Follow Material3 motion and animation guidelines
- Implement proper loading states and error handling
- Support both light and dark themes
- Handle offline scenarios gracefully

#### Testing
- Write unit tests for ViewModels and business logic
- Create UI tests for key user flows
- Test on different screen sizes and orientations

### 9. Specific Component Requirements

#### Specimen Card (Grid Mode)
```kotlin
@Composable
fun SpecimenCard(
    specimen: Specimen,
    onCardClick: () -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onCardClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // AsyncImage for specimen photo
            // Species name with proper typography
            // Location with Material icon
            // Period chip
            // Action overflow button
        }
    }
}
```

#### Filter Section
```kotlin
@Composable
fun FilterSection(
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit,
    displayMode: DisplayMode,
    onDisplayModeChange: (DisplayMode) -> Unit,
    resultCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sort dropdown
        // Result counter
        // Display mode toggle
    }
}
```

## Expected Deliverables

1. **HomeScreen.kt** - Main composable with proper Material3 theming
2. **HomeViewModel.kt** - ViewModel with reactive state management
3. **Specimen data models** - Kotlin data classes
4. **Component composables** - Cards, filters, lists following Material3
5. **Navigation setup** - Jetpack Navigation with proper destinations
6. **Theme implementation** - Material3 colors, typography, shapes
7. **Proper Android patterns** - FAB, Bottom sheets, dialogs, etc.

## Success Criteria

✅ **Native Android Feel**: Uses Material3 components and follows Android UX patterns
✅ **Professional Quality**: Polished UI with proper spacing, typography, and colors  
✅ **Functional Parity**: All iOS features work equivalently on Android
✅ **Performance**: Smooth scrolling, efficient image loading, responsive interactions
✅ **Accessibility**: Full TalkBack support and proper semantic labeling
✅ **Responsive**: Works well on different screen sizes and orientations
✅ **Platform Appropriate**: Leverages Android-specific patterns where they improve UX

The final Android implementation should feel like a native Android app that happens to have the same features as the iOS version, not like an iOS app ported to Android.