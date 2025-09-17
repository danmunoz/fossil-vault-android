# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FossilVault is a comprehensive fossil collection management app for Android, built with Kotlin and Jetpack Compose. This is the Android version of a production iOS app that helps fossil collectors catalog, organize, and manage their fossil collections.

**Official Website**: https://fossilvault.app
**Privacy Policy**: https://fossilvault.app/privacy/
**Target Market**: Paleontology enthusiasts and fossil collectors worldwide
**Development Base**: Berlin, Germany

The iOS version features:
- Complete specimen cataloging with 25+ fields (species, geological period, location, dimensions, value)
- Photo management with multiple images per specimen
- GPS location tracking with interactive maps
- Multi-currency value tracking (25 currencies)
- Search and filtering by geological periods
- Export functionality (CSV, ZIP)
- Statistics and analytics dashboard
- Currently free with potential future monetization strategies

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
│   │   ├── FossilVaultApplication.kt       # Application class with Hilt
│   │   ├── MainActivity.kt                 # Main entry point
│   │   ├── authentication/                 # Complete auth system
│   │   │   ├── data/AndroidFBAuthentication.kt
│   │   │   ├── domain/                     # Auth business logic
│   │   │   └── ui/                         # Auth screens & components
│   │   ├── data/                           # Data layer implementation
│   │   │   ├── di/                         # Dependency injection modules
│   │   │   ├── local/                      # Room database (DAO, entities)
│   │   │   ├── models/                     # Core data models
│   │   │   └── repository/                 # Repository implementations
│   │   └── ui/                             # UI layer
│   │       ├── screens/                    # Feature screens
│   │       │   ├── welcome/                # Welcome/onboarding
│   │       │   ├── home/                   # Home screen with specimens
│   │       │   └── detail/                 # Fossil detail view (in progress)
│   │       └── theme/                      # Material3 design system
│   │           ├── Color.kt                # Base colors
│   │           ├── PeriodColors.kt         # Geological period colors
│   │           ├── Dimensions.kt           # Spacing system
│   │           ├── Theme.kt                # Theme composition
│   │           └── Type.kt                 # Typography scale
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
- **Architecture**: MVVM with Hilt dependency injection implemented
- **Authentication**: Complete Firebase Auth system with email/password signup/login
- **Data Layer**: Repository pattern with Firestore and Room database integration
- **Theme System**: Material3 with geological period color system and typography scale
- **Core Screens**: Welcome screen, Home screen with specimen display, Detail screen (in progress)
- **Navigation**: Jetpack Navigation Compose with authentication flow
- **Firebase**: Firestore, Authentication, and Storage configured

### Implementation Approach
The Android app should maintain feature parity with the iOS version while following Android design patterns:

1. **Design System Migration**: Convert iOS SwiftUI design system to Material3/Compose
   - Geological period colors (13 unique periods: Precambrian → Quaternary)  
   - 5-level typography hierarchy
   - Consistent spacing and layout patterns
   - Light/dark theme support

2. **Architecture Pattern**: ✅ IMPLEMENTED
   - MVVM with Hilt dependency injection
   - Repository pattern with Firestore and Room
   - StateFlow for reactive state management
   - Clean architecture with domain/data/ui layers

3. **Firebase Integration**: ✅ IMPLEMENTED
   - Firestore for cloud data storage
   - Firebase Auth for user authentication
   - Firebase Storage for image management
   - Configuration complete with security rules

4. **Core Features**:
   - ✅ **Authentication**: Complete email/password system
   - ✅ **Welcome Screen**: Onboarding with animated logo and features
   - ✅ **Home Screen**: Specimen display with search/filter capabilities
   - 🚧 **Detail Screen**: In progress - comprehensive specimen view
   - ⏳ **Photo Management**: Multi-image gallery per specimen
   - ⏳ **Location Picker**: Google Maps integration
   - ⏳ **Export Functionality**: CSV, ZIP export
   - ⏳ **Statistics Dashboard**: Analytics and insights
   - ⏳ **Settings**: User preferences and app configuration

## Key Implementation Notes

### Design System
- ✅ **Period Colors**: Complete geological period color system (13 periods)
- ✅ **Typography**: 5-level hierarchy with proper scaling and accessibility
- ✅ **Spacing System**: Consistent dimensions with Dimensions.kt
- ✅ **Material3 Integration**: Custom theme with period colors
- 🚧 **Dark Theme**: Basic support implemented, refinement needed
- ⏳ **Dynamic Colors**: Android 12+ theming integration

### Data Model
✅ **Complete Specimen Model**: 25+ fields implemented in `data/models/Specimen.kt`:
- **Basic**: species, geological period, element type, inventory ID
- **Location**: text location, formation, GPS coordinates
- **Physical**: dimensions with multi-unit support (mm, cm, inches)
- **Financial**: price paid, estimated value with 25 currency support
- **Metadata**: tags, notes, discovery/acquisition dates, favorite status
- **Storage**: Both Firestore (`FirestoreSpecimen.kt`) and Room (`SpecimenEntity.kt`) entities

### Navigation
✅ **Navigation System**: Jetpack Navigation Compose implemented
- **Authentication Flow**: Welcome → Login/Signup → Home
- **Main Navigation**: Home screen with specimen grid/list toggle
- **Detail Navigation**: Home → Detail screen (in progress)
- 🚧 **Modal Navigation**: Detail screen as modal presentation
- ⏳ **Bottom Navigation**: For main app sections (Home, Search, Settings, Profile)

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

### Completed (Phase 1-4)
1. ✅ **Design System**: Colors, typography, spacing, Material3 integration
2. ✅ **Data Models**: Complete specimen model with Firebase/Room integration
3. ✅ **Core Screens**: Welcome, Authentication, Home screens with MVVM
4. ✅ **Navigation**: Authentication flow and basic app navigation

### In Progress (Phase 5)
5. 🚧 **Detail Screen**: Comprehensive specimen view with image gallery
   - Species classification, physical properties, location discovery
   - Value/inventory tracking, image management

### Next Steps (Phase 6-7)
6. ⏳ **Advanced Features**: Search refinement, export, analytics dashboard
7. ⏳ **Testing & Polish**: Comprehensive testing, performance optimization, accessibility

## Firebase Configuration
✅ **Firebase Setup Complete**: All core services configured and operational
- ✅ **Firestore**: Database with security rules for user data isolation
- ✅ **Authentication**: Email/password auth with user profile management
- ✅ **Storage**: Cloud storage configured for specimen images
- ✅ **Configuration**: google-services.json integrated with build system
- ✅ **Privacy Policy**: https://fossilvault.app/privacy/ integrated in AndroidManifest.xml
- ⏳ **Functions**: Server-side logic for data processing and exports (future)

## App Store & Distribution
- **Privacy Policy**: Hosted at https://fossilvault.app/privacy/ for Google Play compliance
- **Camera Permissions**: Privacy policy covers camera usage for specimen photography
- **Location Permissions**: GPS tracking for fossil discovery sites covered in privacy policy
- **Target Platforms**: iOS (production), Android (beta development)