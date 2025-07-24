# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FossilVault is a comprehensive fossil collection management app for Android, built with Kotlin and Jetpack Compose. This is the Android version of a production iOS app that helps fossil collectors catalog, organize, and manage their fossil collections.

The iOS version features:
- Complete specimen cataloging with 25+ fields (species, geological period, location, dimensions, value)
- Photo management with multiple images per specimen
- GPS location tracking with interactive maps
- Multi-currency value tracking (25 currencies)
- Search and filtering by geological periods
- Export functionality (CSV, ZIP)
- Statistics and analytics dashboard
- Freemium business model with Pro tier subscriptions

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material3
- **Architecture**: MVVM (to be implemented)
- **Backend**: Firebase (Firestore, Auth, Storage, Functions)
- **Build System**: Gradle with Kotlin DSL
- **Min SDK**: 26 (Android 8.0+)
- **Target SDK**: 36 (latest)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/dmdev/fossilvaultanda/
│   │   ├── MainActivity.kt                 # Main entry point
│   │   └── ui/theme/                       # Material3 theme system
│   │       ├── Color.kt                    # Color definitions
│   │       ├── Theme.kt                    # Theme composition
│   │       └── Type.kt                     # Typography definitions
│   ├── AndroidManifest.xml                # App configuration
│   └── res/                                # Resources (strings, layouts, etc.)
├── src/test/                               # Unit tests
└── src/androidTest/                        # Instrumentation tests
```

## Build Commands

### Development
```bash
# Build debug version
./gradlew assembleDebug

# Build and install on device/emulator
./gradlew installDebug

# Clean build
./gradlew clean

# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.dmdev.fossilvaultanda.ExampleUnitTest"

# Generate APK
./gradlew assembleRelease
```

### Code Quality
```bash
# Run lint checks
./gradlew lint

# Run all checks (lint + test)
./gradlew check
```

## Architecture Guidelines

### Current State
- **Basic Setup**: Standard Android Studio template with Jetpack Compose
- **Theme System**: Material3 with basic color scheme (placeholder colors)
- **Package Structure**: Single package `com.dmdev.fossilvaultanda`

### Implementation Approach
The Android app should maintain feature parity with the iOS version while following Android design patterns:

1. **Design System Migration**: Convert iOS SwiftUI design system to Material3/Compose
   - Geological period colors (13 unique periods: Precambrian → Quaternary)  
   - 5-level typography hierarchy
   - Consistent spacing and layout patterns
   - Light/dark theme support

2. **Architecture Pattern**: Implement MVVM with:
   - ViewModels for state management
   - Repository pattern for data access
   - Dependency injection (Hilt recommended)
   - Flow/StateFlow for reactive programming

3. **Firebase Integration**: 
   - Firestore for cloud data storage
   - Firebase Auth for user authentication
   - Firebase Storage for image management
   - Firebase Functions for server-side logic

4. **Core Features to Implement**:
   - Specimen catalog with comprehensive data model
   - Photo gallery with multiple images per specimen
   - Location picker with Google Maps integration
   - Search and filtering by geological periods
   - Export functionality (CSV, ZIP)
   - Statistics dashboard
   - Settings and user preferences

## Key Implementation Notes

### Design System
- Replace default Material3 colors with FossilVault geological period color system
- Implement typography scale matching iOS design specifications
- Ensure accessibility compliance with proper contrast ratios
- Support dynamic color theming on Android 12+

### Data Model
The core Specimen model should include 25+ fields:
- Basic info: species, geological period, element type, inventory ID
- Location: text location, formation, GPS coordinates  
- Physical: width/height/length with unit selection
- Financial: price paid, estimated value with multi-currency support
- Metadata: tags, notes, collection dates, favorite status

### Navigation
- Use Jetpack Navigation Compose for screen navigation
- Implement bottom navigation or navigation drawer for main sections
- Modal presentations for forms and detail views

### Testing Strategy
- Unit tests for ViewModels and business logic
- UI tests for Compose screens using ComposeTestRule
- Integration tests for Firebase interactions
- Screenshot tests for design system components

## Package Naming Convention
- `ui.screens.*` - Screen-level Composables and ViewModels
- `ui.components.*` - Reusable UI components
- `ui.theme.*` - Design system (colors, typography, spacing)
- `data.*` - Data layer (repositories, data sources)
- `domain.*` - Business logic and use cases
- `util.*` - Utility functions and extensions

## Development Workflow
1. Implement design system components first (colors, typography, basic components)
2. Create core data models and Firebase integration
3. Build main screens following MVVM pattern
4. Add navigation between screens
5. Implement advanced features (search, export, analytics)
6. Add comprehensive testing
7. Optimize performance and accessibility

## Firebase Configuration
Firebase configuration files (google-services.json) and API keys should be added during development phase. The app will need:
- Firestore rules for data security
- Firebase Auth configuration
- Cloud Storage rules for image uploads
- Firebase Functions for server-side operations