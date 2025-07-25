# Android Fossil Detail Screen Implementation Prompt

## Project Context
You are implementing an Android version of a Fossil Detail screen for a fossil collection app. This screen displays comprehensive information about individual fossil specimens. **CRITICAL: You must use native Android design patterns, Material Design principles, and Android-specific UI components throughout. Do not replicate iOS design patterns - instead, create the best possible Android user experience.**

## Core Requirements

### Screen Purpose
Create a detail screen that displays fossil specimen information in a user-friendly, organized manner following Material Design 3 principles and modern Android UX patterns.

### Architecture Requirements
- **Language**: Kotlin
- **Framework**: Jetpack Compose
- **Architecture**: MVVM with StateFlow/LiveData
- **Navigation**: Navigation Component with arguments
- **Image Loading**: Coil or Glide
- **State Management**: ViewModel with proper lifecycle awareness

## Data Model Reference
```kotlin
data class Specimen(
    val id: String,
    val species: String,
    val element: String, // e.g., "Shell", "Bone", "Leaf"
    val period: GeologicalPeriod,
    val imageUrls: List<String>,
    val tagNames: List<String>,
    
    // Location data (all optional)
    val location: String?,
    val formation: String?,
    val latitude: Double?,
    val longitude: Double?,
    val collectionDate: Date?,
    val acquisitionDate: Date?,
    
    // Physical properties (all optional)
    val width: Double?,
    val height: Double?,
    val length: Double?,
    val unit: MeasurementUnit, // cm, mm, inches
    
    // Value data (all optional)
    val pricePaid: Double?,
    val pricePaidCurrency: Currency?,
    val estimatedValue: Double?,
    val estimatedValueCurrency: Currency?,
    val inventoryId: String?,
    val notes: String?,
    
    val creationDate: Date
)
```

## Android-Native Design Implementation

### 1. App Bar & Navigation (Material Design 3)
**Use**: `TopAppBar` with Material 3 styling
- **Back Navigation**: Use `NavigationIcon` with proper Material icon
- **Title**: "Fossil Details" following Material typography scale
- **Actions**: Use `IconButton` with overflow menu (`MoreVert` icon)
- **Behavior**: Implement collapsing toolbar if image gallery is large
- **Elevation**: Follow Material 3 elevation tokens

### 2. Image Gallery (Android-Native Approach)
**Primary Option**: `HorizontalPager` from Accompanist Pager
- **Implementation**: Modern ViewPager2 equivalent in Compose
- **Indicators**: Use `PagerIndicator` below the images
- **Aspect Ratio**: Maintain aspect ratio with `Modifier.aspectRatio()`
- **Loading**: Coil with placeholder and error states
- **Full Screen**: Navigate to separate fragment/composable with shared element transition

**Alternative**: If multiple images, use `LazyRow` with snap behavior
- **Card Style**: Each image in Material Card with elevation
- **Spacing**: Proper padding between images
- **Accessibility**: Proper content descriptions

### 3. Content Organization (Material Cards & Lists)
**DO NOT use iOS-style cards**. Instead use:

#### Material Design 3 Cards with Proper Hierarchy
```kotlin
// Species & Classification Section
Card(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Hero content with Material typography
        Text(
            text = specimen.species,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = specimen.element,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        
        // Period chip using Material FilterChip
        FilterChip(
            selected = false,
            onClick = { /* Handle period info */ },
            label = { Text(specimen.period.name) },
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = getPeriodColor(specimen.period),
                            shape = CircleShape
                        )
                )
            }
        )
        
        // Tags using FlowRow with AssistChips
        FlowRow {
            specimen.tagNames.forEach { tag ->
                AssistChip(
                    onClick = { /* Handle tag click */ },
                    label = { Text(tag) }
                )
            }
        }
    }
}
```

#### Location Section (Android List Pattern)
**DO NOT replicate iOS row pattern**. Use Material Design list items:
```kotlin
LazyColumn {
    if (hasLocationData) {
        item {
            Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Column {
                    // Section header with Material design
                    ListItem(
                        headineContent = { Text("Location & Discovery") },
                        leadingContent = {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    
                    Divider()
                    
                    // Location items using ListItem
                    specimen.location?.let { location ->
                        ListItem(
                            headlineContent = { Text(location) },
                            supportingContent = { Text("Found at") },
                            leadingContent = {
                                Icon(Icons.Default.Place, contentDescription = null)
                            }
                        )
                    }
                    
                    // Coordinates with map integration
                    if (specimen.hasCoordinates()) {
                        ListItem(
                            headlineContent = { Text(formatCoordinates()) },
                            supportingContent = { Text("Coordinates") },
                            leadingContent = {
                                Icon(Icons.Default.Map, contentDescription = null)
                            },
                            trailingContent = {
                                IconButton(onClick = { openMap() }) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = "Open in Maps")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
```

### 4. Action Handling (Material Bottom Sheet & Menus)
**DO NOT use iOS action sheets**. Use Android patterns:

#### Overflow Menu (DropdownMenu)
```kotlin
var showMenu by remember { mutableStateOf(false) }

IconButton(onClick = { showMenu = true }) {
    Icon(Icons.Default.MoreVert, contentDescription = "More options")
}

DropdownMenu(
    expanded = showMenu,
    onDismissRequest = { showMenu = false }
) {
    DropdownMenuItem(
        text = { Text("Edit fossil") },
        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
        onClick = {
            showMenu = false
            onEditClick()
        }
    )
    DropdownMenuItem(
        text = { Text("Share") },
        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
        onClick = {
            showMenu = false
            onShareClick()
        }
    )
    // ... more items
}
```

#### Bottom Sheet for Secondary Actions
For complex actions, use Material Bottom Sheet:
```kotlin
if (showBottomSheet) {
    ModalBottomSheet(
        onDismissRequest = { showBottomSheet = false }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Fossil Actions",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Action items using NavigationDrawerItem pattern
            NavigationDrawerItem(
                label = { Text("Export for Auction") },
                icon = { Icon(Icons.Default.FileUpload, contentDescription = null) },
                selected = false,
                onClick = { /* Handle export */ }
            )
            // ... more actions
        }
    }
}
```

### 5. Android-Specific Patterns to Implement

#### Shared Element Transitions
```kotlin
// Use shared element transitions for image viewing
SharedTransitionLayout {
    // Gallery to full-screen transition
    AnimatedContent(targetState = isFullScreen) { fullScreen ->
        if (fullScreen) {
            FullScreenImageViewer(
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "image_${currentImageIndex}"),
                    animatedVisibilityScope = this
                )
            )
        } else {
            ImageGallery(
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "image_${currentImageIndex}"),
                    animatedVisibilityScope = this
                )
            )
        }
    }
}
```

#### Adaptive Layout for Tablets
```kotlin
@Composable
fun FossilDetailScreen() {
    val isTablet = LocalConfiguration.current.screenWidthDp >= 600
    
    if (isTablet) {
        // Two-pane layout for tablets
        Row {
            // Image gallery on left
            Box(modifier = Modifier.weight(1f)) {
                ImageGallery()
            }
            
            // Details on right
            LazyColumn(modifier = Modifier.weight(1f)) {
                // Detail cards
            }
        }
    } else {
        // Single column for phones
        LazyColumn {
            item { ImageGallery() }
            item { DetailCards() }
        }
    }
}
```

#### Material Motion & Animations
```kotlin
// Use Material motion patterns
LazyColumn {
    itemsIndexed(detailSections) { index, section ->
        Card(
            modifier = Modifier
                .animateItemPlacement()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Section content with enter animation
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = index * 100
                    )
                )
            ) {
                SectionContent(section)
            }
        }
    }
}
```

### 6. Android Integration Points

#### System Integration
- **Share Intent**: Use Android's native sharing with proper MIME types
- **Maps Integration**: Open coordinates in default maps app using Intent
- **Copy to Clipboard**: Use ClipboardManager for inventory IDs
- **Camera Integration**: If editing, use proper camera/gallery intents

#### Accessibility (TalkBack Support)
```kotlin
// Proper semantics for screen readers
Card(
    modifier = Modifier.semantics {
        contentDescription = "Species information for ${specimen.species}"
        heading()
    }
) {
    // Content with proper semantics
    Text(
        text = specimen.species,
        modifier = Modifier.semantics {
            heading()
        }
    )
}
```

#### State Persistence
```kotlin
// Handle configuration changes properly
@Composable
fun FossilDetailScreen(
    viewModel: FossilDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Save/restore scroll position
    val listState = rememberLazyListState()
    
    LaunchedEffect(uiState.specimenId) {
        // Restore scroll position if needed
        if (uiState.savedScrollPosition > 0) {
            listState.scrollToItem(uiState.savedScrollPosition)
        }
    }
}
```

## Implementation Priority

### Phase 1: Core Structure
1. Basic screen with TopAppBar and navigation
2. Image gallery with HorizontalPager
3. Species information card with Material design
4. Basic overflow menu with primary actions

### Phase 2: Information Cards
1. Location card with ListItem pattern
2. Physical properties with measurement display
3. Value information with proper currency formatting
4. Conditional rendering logic

### Phase 3: Android Polish
1. Shared element transitions
2. Tablet adaptive layout
3. Proper accessibility support
4. System integration (share, maps, clipboard)
5. Material motion animations

## Key Android Principles to Follow

1. **Material Design 3**: Use latest Material components and tokens
2. **Adaptive Design**: Support different screen sizes and orientations
3. **System Integration**: Leverage Android's built-in capabilities
4. **Performance**: Optimize for smooth scrolling and image loading
5. **Accessibility**: Full TalkBack and accessibility service support
6. **State Management**: Proper ViewModel and state handling
7. **Navigation**: Use Navigation Component with proper back stack

## Success Criteria

The implementation should feel distinctly Android while providing the same information and functionality as the iOS version. Users should recognize native Android patterns and feel comfortable navigating the interface using familiar Android interaction patterns.

**Remember**: This is NOT a port of the iOS interface - it's a native Android implementation that happens to show the same data. Prioritize Android usability and conventions over visual similarity to iOS.