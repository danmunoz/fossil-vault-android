# FossilVault Data Models - Platform-Agnostic Guide

## Overview

This document provides comprehensive specifications for the FossilVault data models, designed to be implemented consistently across iOS, Android, Web, and other platforms. All models support JSON serialization and are designed for Firebase Firestore integration.

## Core Data Models

### 1. Specimen Model

The `Specimen` model represents a fossil specimen in a user's collection. It contains comprehensive metadata about the fossil including scientific information, location data, measurements, valuation, and associated media.

#### Properties

```typescript
interface Specimen {
    // Core Identity
    id: string                              // Unique identifier
    userId: string                          // Owner's user ID
    species: string                         // Scientific species name
    period: Period                          // Geological period (enum)
    element: FossilElement                  // Type of fossil element (enum)
    
    // Location Information
    location?: string                       // Collection location name
    formation?: string                      // Geological formation
    latitude?: number                       // GPS latitude coordinate
    longitude?: number                      // GPS longitude coordinate
    
    // Physical Measurements
    width?: number                          // Width measurement
    height?: number                         // Height measurement
    length?: number                         // Length measurement
    unit: SizeUnit                          // Measurement unit (enum)
    
    // Dates
    collectionDate?: Date                   // When fossil was collected
    acquisitionDate?: Date                  // When added to collection
    creationDate: Date                      // Record creation timestamp
    
    // Additional Metadata
    inventoryId?: string                    // Custom inventory identifier
    notes?: string                          // User notes and observations
    
    // Media
    imageUrls: StoredImage[]                // Associated images
    
    // Organization
    isFavorite: boolean                     // User favorite flag
    tagNames: string[]                      // Associated tag names
    isPublic: boolean                       // Public visibility flag
    
    // Valuation
    pricePaid?: number                      // Purchase price
    pricePaidCurrency?: Currency            // Purchase currency
    estimatedValue?: number                 // Current estimated value
    estimatedValueCurrency?: Currency       // Valuation currency
}
```

#### Validation Rules

- `id`: Must be non-empty, unique string
- `userId`: Must be valid user identifier
- `species`: Required, non-empty string
- `period`: Must be valid Period enum value
- `element`: Must be valid FossilElement enum value
- `unit`: Must be valid SizeUnit enum value, defaults to `mm`
- `creationDate`: Required, defaults to current timestamp
- `imageUrls`: Array of valid StoredImage objects
- `isFavorite`: Required boolean, defaults to `false`
- `tagNames`: Array of lowercase strings
- `isPublic`: Required boolean, defaults to `false`
- GPS coordinates: Valid latitude (-90 to 90), longitude (-180 to 180)
- Measurements: Non-negative numbers when present
- Prices: Non-negative numbers when present

#### CSV Export Support

The Specimen model supports CSV export with the following headers:
```
Identifier, Species, Period, Element, Location, Formation, Latitude, Longitude, 
Width, Height, Length, Unit, Collection Date, Acquisition Date, Inventory ID, 
Notes, Creation Date, Price Paid, Price Paid Currency, Estimated Value, 
Estimated Value Currency
```

#### Hash Implementation
- Hash based on `id` property only for efficient collection operations

### 2. Tag Model

Simple tagging system for organizing specimens with user-specific tags.

```typescript
interface Tag {
    id: string          // Computed from name (name serves as ID)
    userId?: string     // Owner's user ID (null for system tags)
    name: string        // Tag name (automatically lowercase)
}
```

#### Validation Rules
- `name`: Required, automatically converted to lowercase
- `userId`: Optional, used for user-specific tags
- `id`: Computed property, equals `name`

#### Usage Patterns
- Tags are referenced in specimens by name via `tagNames` array
- User-specific tags have `userId` set
- System/global tags have `userId` as null
- Tag names are case-insensitive (stored lowercase)

### 3. UserProfile Model

User account information and preferences.

```typescript
interface UserProfile {
    // Identity
    userId: string              // Unique user identifier
    email: string               // User email address
    
    // Profile Information
    fullName?: string           // Full display name
    username?: string           // Username/handle
    location?: string           // User location
    bio?: string               // User biography/description
    
    // Settings
    isPublic: boolean          // Profile visibility
    picture?: StoredImage      // Profile picture
    settings: AppSettings      // App preferences
}
```

#### Validation Rules
- `userId`: Required, unique identifier
- `email`: Required, valid email format
- `isPublic`: Required boolean, defaults to `false`
- `settings`: Required AppSettings object with defaults

### 4. Supporting Models

#### StoredImage
Represents an image stored in cloud storage with URL and path information.

```typescript
interface StoredImage {
    url: string         // Public download URL
    path: string        // Storage path for management
}
```

#### AppSettings
User application preferences affecting app behavior.

```typescript
interface AppSettings {
    unit: SizeUnit                  // Default measurement unit
    divideCarboniferous: boolean    // Split Carboniferous period
    defaultCurrency: Currency       // Preferred currency
}
```

**Defaults:**
- `unit`: `SizeUnit.mm`
- `divideCarboniferous`: `false`
- `defaultCurrency`: Device/region currency or USD fallback

## Enumeration Types

### Period (Geological Periods)
```typescript
enum Period {
    precambrian = "precambrian"
    cambrian = "cambrian"
    ordovician = "ordovician"
    silurian = "silurian"
    devonian = "devonian"
    carboniferous = "carboniferous"      // Can be split based on settings
    mississippian = "mississippian"      // Carboniferous subdivision
    pennsylvanian = "pennsylvanian"      // Carboniferous subdivision
    permian = "permian"
    triassic = "triassic"
    jurassic = "jurassic"
    cretaceous = "cretaceous"
    paleocene = "paleocene"              // Display as "Paleogene"
    neogene = "neogene"
    quaternary = "quaternary"
    unknown = "unknown"
}
```

#### Dynamic Behavior
- `allCases` property returns different arrays based on `AppSettings.divideCarboniferous`
- When `divideCarboniferous` is true, includes `mississippian` and `pennsylvanian`
- When false, includes `carboniferous` instead

### SizeUnit (Measurement Units)
```typescript
enum SizeUnit {
    mm = "mm"           // Millimeters
    cm = "cm"           // Centimeters
    inch = "inch"       // Inches
}
```

### Currency (International Currencies)
```typescript
enum Currency {
    usd = "USD", eur = "EUR", gbp = "GBP", jpy = "JPY",
    cad = "CAD", aud = "AUD", chf = "CHF", cny = "CNY",
    sek = "SEK", nok = "NOK", dkk = "DKK", pln = "PLN",
    czk = "CZK", huf = "HUF", rub = "RUB", brl = "BRL",
    inr = "INR", krw = "KRW", mxn = "MXN", sgd = "SGD",
    hkd = "HKD", nzd = "NZD", zar = "ZAR", try = "TRY",
    ils = "ILS", aed = "AED", thb = "THB", myr = "MYR"
}
```

#### Currency Features
- **Symbol mapping**: Each currency has appropriate symbol (`$`, `€`, `£`, etc.)
- **Display names**: Full currency names ("US Dollar", "Euro", etc.)
- **Device default**: Automatic selection based on user's device region
- **Multi-currency calculations**: Support for mixed-currency collections

### FossilElement (Fossil Types)
```typescript
enum FossilElement {
    tooth = "tooth"
    jaw = "jaw"
    skull = "skull"
    bone = "bone"
    claw = "claw"
    horn = "horn"
    rib = "rib"
    vertebra = "vertebra"
    shell = "shell"
    ammonite = "ammonite"
    matrix = "matrix"
    coprolite = "coprolite"
    imprint = "imprint"
    track = "track"
    egg = "egg"
    other = "other"     // Custom value storage
}
```

#### Special Handling
- `other` case can store custom string values
- Display strings are capitalized versions of raw values
- Custom "other" values preserve original casing

## Multi-Currency Support

### MultiCurrencyValue
Complex type for handling collections with multiple currencies.

```typescript
interface MultiCurrencyValue {
    totals: Map<Currency, number>       // Amount per currency
    primaryCurrency: Currency           // User's preferred currency
    specimenCount: number              // Number of specimens included
}
```

#### Computed Properties
- `primaryTotal`: Amount in primary currency
- `hasMultipleCurrencies`: Boolean indicating mixed currencies
- `currencyCount`: Number of different currencies
- `sortedCurrencies`: Currencies sorted by amount (descending)
- `totalAmount`: Sum of all amounts (single-currency collections)
- `isEmpty`: True if no financial data

#### Currency Calculations
- **Total Spent**: Calculates from `Specimen.pricePaid` and `pricePaidCurrency`
- **Estimated Value**: Uses `estimatedValue` or falls back to `pricePaid`
- **Formatting**: Intelligent display showing primary currency or breakdown
- **Regional Support**: Currency symbols and formatting per locale

## Platform Implementation Notes

### Serialization
- All models support JSON serialization for Firebase Firestore
- Date fields should use ISO8601 format for cross-platform compatibility
- Enum values are stored as raw string values
- Optional fields should be nullable/omittable in target language

### Validation
- Implement validation at the model level for data integrity
- Use appropriate type constraints (non-negative numbers, valid emails)
- Validate GPS coordinates within valid ranges
- Ensure required fields are always present

### Platform-Specific Considerations

#### iOS/Swift
- Use `Codable` protocol for JSON serialization
- Implement custom `CodingKeys` for field mapping
- Use `LocalizedStringResource` for period names
- Handle migration for legacy data (especially `FossilElement`)

#### Android/Kotlin
- Use `@Serializable` with kotlinx.serialization
- Implement `@SerialName` for field mapping
- Use `LocalContext` for currency locale detection
- Handle null safety properly for optional fields

#### Web/TypeScript
- Use interfaces for type definitions
- Implement JSON serialization/deserialization functions
- Use `Intl.NumberFormat` for currency formatting
- Handle timezone conversion for date fields

#### Flutter/Dart
- Use `json_annotation` for serialization
- Implement `@JsonKey` for field mapping
- Use `intl` package for currency and date formatting
- Handle null safety with proper typing

### Testing Strategy
- Create mock data generators for each model
- Test serialization/deserialization round-trips
- Validate enum parsing and invalid value handling
- Test currency calculations with edge cases
- Verify CSV export formatting and special character handling

This specification ensures consistent data handling across all platforms while respecting platform-specific conventions and optimizations.