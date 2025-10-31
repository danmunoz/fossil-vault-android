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

## iOS Counterpart Project

**Location**: `/Users/danielmunoz/Repos/fossil-collector`

This is the production iOS version of FossilVault, built with SwiftUI. When working on the Android version:
- Reference iOS implementations for feature parity and design consistency
- Compare data models, UI patterns, and business logic between platforms
- Use iOS code as the source of truth for app behavior and features
- Files from the iOS project can be referenced using the "@" command

**Permission Note**: Claude Code has permission to read all files from the iOS project at `/Users/danielmunoz/Repos/fossil-collector` to facilitate cross-platform development and ensure feature parity.

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material3
- **Architecture**: MVVM with Hilt (implemented)
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
│   │       │   ├── detail/                 # Fossil detail view with image gallery
│   │       │   ├── specimen/               # Add/edit specimen forms
│   │       │   ├── profile/                # User profile & account management
│   │       │   ├── settings/               # App settings & preferences
│   │       │   ├── stats/                  # Statistics & analytics dashboard
│   │       │   ├── support/                # FAQ & about screens
│   │       │   └── subscription/           # Subscription & usage limits
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
- **Architecture**: MVVM with Hilt dependency injection fully implemented
- **Authentication**: Complete Firebase Auth system with email/password signup/login
- **Data Layer**: Repository pattern with Firestore and Room database integration
- **Theme System**: Material3 with geological period color system and typography scale
- **Core Screens**: All primary screens implemented (Welcome, Home, Detail, Add/Edit Specimen, Profile, Settings, Stats, Support)
- **Navigation**: Jetpack Navigation Compose with complete app navigation flow
- **Firebase**: Firestore, Authentication, and Storage fully configured and operational
- **Image Management**: Multi-image gallery with full-screen viewer, zoom, and carousel navigation
- **User Features**: Profile management, settings configuration, subscription limits, FAQ/support

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
   - ✅ **Authentication**: Complete email/password system with profile management
   - ✅ **Welcome Screen**: Onboarding with animated logo and feature highlights
   - ✅ **Home Screen**: Specimen grid/list display with search, filter, and empty states
   - ✅ **Detail Screen**: Comprehensive specimen view with all information cards
   - ✅ **Photo Management**: Multi-image gallery with full-screen viewer and zoom
   - ✅ **Add/Edit Specimen**: Complete form with image picker, location, geological time, dimensions
   - ✅ **Profile Management**: User profile editing, account management
   - ✅ **Settings**: Currency preferences, size units, app configuration
   - ✅ **Statistics Dashboard**: Analytics and collection insights
   - ✅ **Support**: FAQ, About screens with app information
   - 🚧 **Subscription/Limits**: Usage limits and upgrade prompts (in progress)
   - ⏳ **Location Picker**: Google Maps integration for specimen discovery sites
   - ⏳ **Export Functionality**: CSV, ZIP export of collection data

## Recent Updates

### Latest Features (v0.5 - Current)
- **Enhanced Detail Screen**: Improved image viewer with zoomable images, full-screen carousel navigation, and thumbnail preview
- **Refined Home UI**: Added comprehensive empty state views, improved navigation buttons, grid/list toggle
- **Subscription System**: Base structure for usage limits with LimitReachedScreen
- **Image Management**: ImageThumbnailCarousel with swipe navigation, FullScreenImageViewer with pinch-to-zoom
- **UI Polish**: Improved specimen cards, better spacing and visual hierarchy across all screens

### Component Highlights
- **ImageThumbnailCarousel**: Horizontal scrollable image gallery with thumbnail indicators
- **ZoomableImageView**: Pinch-to-zoom and pan support for specimen photos
- **FullScreenImageViewer**: Modal full-screen image display with carousel navigation
- **EmptyViews**: Context-aware empty states for no specimens, no search results, no favorites
- **SpecimenCard**: Optimized card display with period colors, favorite badges, image previews

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
✅ **Navigation System**: Jetpack Navigation Compose fully implemented
- **Authentication Flow**: Welcome → Login/Signup → Home
- **Main Navigation**: Bottom navigation bar with Home, Stats, Settings, Profile tabs
- **Detail Navigation**: Home → Detail screen with full-screen image viewer
- **Form Navigation**: Add/Edit Specimen with specialized picker screens (Period, Element, Location, Currency, Size Units)
- **Settings Navigation**: Settings → Currency/Size Unit pickers, FAQ, About screens
- **Profile Navigation**: Profile → Edit Profile screen with account management

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

### Completed (Phase 1-7)
1. ✅ **Design System**: Complete Material3 theme with geological period colors, typography, spacing
2. ✅ **Data Models**: Full specimen model with Firebase/Room integration, 25+ fields
3. ✅ **Authentication**: Email/password auth with profile management
4. ✅ **Core Screens**: Welcome, Home (grid/list views, search, filter, empty states)
5. ✅ **Detail Screen**: Comprehensive view with image gallery, full-screen viewer, all info cards
6. ✅ **Add/Edit Specimen**: Complete form with image picker, specialized pickers (period, element, location, currency, size units)
7. ✅ **User Management**: Profile screen, edit profile, settings (currency, units, preferences)
8. ✅ **Statistics**: Stats dashboard with collection analytics
9. ✅ **Support**: FAQ and About screens

### In Progress (Phase 8)
10. 🚧 **Subscription & Monetization**: Usage limits, upgrade prompts, subscription management
11. 🚧 **Advanced Image Features**: Image editing, annotation capabilities
12. 🚧 **Dark Theme Refinement**: Polishing dark mode across all screens

### Next Steps (Phase 9-10)
13. ⏳ **Google Maps Integration**: Interactive map for specimen discovery locations
14. ⏳ **Export Functionality**: CSV and ZIP export of collection data with images
15. ⏳ **Advanced Search**: Multi-criteria search, saved searches, search history
16. ⏳ **Testing & Quality**: Comprehensive unit/UI tests, performance optimization
17. ⏳ **Accessibility**: Screen reader support, keyboard navigation, contrast improvements
18. ⏳ **Release Preparation**: Play Store listing, release builds, beta testing

## Firebase Configuration
✅ **Firebase Setup Complete**: All core services configured and operational
- ✅ **Firestore**: Database with security rules for user data isolation
- ✅ **Authentication**: Email/password auth with user profile management
- ✅ **Storage**: Cloud storage configured for specimen images
- ✅ **Configuration**: google-services.json integrated with build system
- ✅ **Privacy Policy**: https://fossilvault.app/privacy/ integrated in AndroidManifest.xml
- ⏳ **Functions**: Server-side logic for data processing and exports (future)

## Feature Parity with iOS

### Implemented Features
- ✅ **Specimen Cataloging**: Complete 25+ field catalog system matching iOS functionality
- ✅ **Photo Management**: Multi-image support with full-screen viewer and zoom
- ✅ **Collection Organization**: Search, filter by geological period, grid/list views
- ✅ **User Profile**: Account management and profile editing
- ✅ **Settings**: Currency preferences (25 currencies), size units, app configuration
- ✅ **Statistics**: Collection analytics and insights dashboard

### Missing Features (vs iOS)
- ⏳ **GPS Location Tracking**: Interactive maps for specimen discovery sites
- ⏳ **Export Functionality**: CSV and ZIP export capabilities
- ⏳ **Advanced Filtering**: Multi-criteria filters, saved searches
- ⏳ **Subscription/Monetization**: Payment integration, premium features

### Android-Specific Enhancements
- Material3 design language with dynamic theming support
- Adaptive layouts for tablets and foldables
- Integration with Android's back gesture navigation
- Support for Android share sheet and intents

## App Store & Distribution
- **Privacy Policy**: Hosted at https://fossilvault.app/privacy/ for Google Play compliance
- **Camera Permissions**: Privacy policy covers camera usage for specimen photography
- **Location Permissions**: GPS tracking for fossil discovery sites covered in privacy policy
- **Target Platforms**: iOS (production), Android (beta development)