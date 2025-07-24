# **Fossil Vault: Platform-Agnostic Design System**

**Version**: 1.0 - July 24, 2025  
**Purpose**: Universal design specification for multi-platform implementation  
**Target Platforms**: iOS (✅ Production), Android (Planned Q3 2025), Web (Planned Q1 2026)

This document defines the complete design system for Fossil Vault across all platforms, providing exact specifications for colors, typography, layout, and components while adapting to platform-specific design languages and constraints.

---

## 🎨 **Universal Color System**

### **Color Philosophy**
The Fossil Vault color system draws inspiration from geological formations and natural history museums, emphasizing scientific accuracy, professional aesthetics, and accessibility across all platforms and themes.

### **Semantic Color Architecture**

All colors support light and dark mode variants with automatic adaptation based on platform capabilities.

#### **Text Color Tokens**
```css
/* Primary text colors with exact values */
--text-primary-light: #111827;     /* RGB(17, 24, 39) */
--text-primary-dark: #F3F4F6;      /* RGB(243, 244, 246) */

--text-secondary-light: #6B7280;   /* RGB(107, 114, 128) */
--text-secondary-dark: #D1D5DB;    /* RGB(209, 213, 219) */

--text-disabled-light: #9CA3AF;    /* RGB(156, 163, 175) */
--text-disabled-dark: #6B7280;     /* RGB(107, 114, 128) */

--text-error: #EF4444;             /* RGB(239, 68, 68) */
--text-placeholder-light: #808080; /* RGB(128, 128, 128) at 45% opacity */
--text-placeholder-dark: #9CA3AF;  /* RGB(156, 163, 175) at 60% opacity */
```

#### **Background Color Tokens**
```css
/* Background and surface colors */
--background-primary-light: #FFFFFF;   /* RGB(255, 255, 255) */
--background-primary-dark: #121214;    /* RGB(18, 18, 20) */

--background-secondary-light: #F2F2F2; /* RGB(242, 242, 242) */
--background-secondary-dark: #1C1C32;  /* RGB(28, 28, 50) */

--background-input-light: #FFFFFF;     /* RGB(255, 255, 255) */
--background-input-dark: #161618;      /* RGB(22, 22, 24) */

--background-card-light: #F2F2F2;      /* RGB(242, 242, 242) */
--background-card-dark: #1C1C32;       /* RGB(28, 28, 50) */
```

#### **Interactive Color Tokens**
```css
/* Interactive elements and icons */
--accent-green-light: #34D399;     /* RGB(52, 211, 153) */
--accent-green-dark: #6EE7B7;      /* RGB(110, 231, 183) */

--accent-blue-light: #60A5FA;      /* RGB(96, 165, 250) */
--accent-blue-dark: #93C5FD;       /* RGB(147, 197, 253) */

--border-light: #E6E6E6;           /* RGB(230, 230, 230) */
--border-dark: #303032;            /* RGB(48, 48, 50) */

--border-focus: #3B82F6;           /* RGB(59, 130, 246) */
--border-error: #EF4444;           /* RGB(239, 68, 68) */
```

#### **Brand Gradient Tokens**
```css
/* Primary brand gradients */
--gradient-primary-start-light: #2563EB; /* RGB(37, 99, 235) */
--gradient-primary-start-dark: #3B82F6;  /* RGB(59, 130, 246) */
--gradient-primary-end: #8B5CF6;         /* RGB(139, 92, 246) */

--gradient-filter-start: #6366F1;        /* RGB(99, 102, 241) */
--gradient-filter-end: #8B5CF6;          /* RGB(139, 92, 246) */
```

### **Geological Period Color System**

Each geological period has unique colors optimized for scientific accuracy and cross-platform consistency:

#### **Period Color Tokens**
```css
/* Geological periods with light/dark variants */
--period-precambrian-light: #808080;  /* RGB(128, 128, 128) */
--period-precambrian-dark: #6366F1;   /* RGB(99, 102, 241) */

--period-cambrian-light: #009ACC;     /* RGB(0, 154, 204) */
--period-cambrian-dark: #06B6D4;      /* RGB(6, 182, 212) */

--period-ordovician-light: #3348E6;   /* RGB(51, 72, 230) */
--period-ordovician-dark: #6366F1;    /* RGB(99, 102, 241) */

--period-silurian-light: #FF9900;     /* RGB(255, 153, 0) */
--period-silurian-dark: #22D3EE;      /* RGB(34, 211, 238) */

--period-devonian-light: #E68033;     /* RGB(230, 128, 51) */
--period-devonian-dark: #F87171;      /* RGB(248, 113, 113) */

--period-carboniferous-light: #4DCC33; /* RGB(77, 204, 51) */
--period-carboniferous-dark: #4ADE80;  /* RGB(74, 222, 128) */

--period-permian-light: #996633;      /* RGB(153, 102, 51) */
--period-permian-dark: #C084FC;       /* RGB(192, 132, 252) */

--period-triassic-light: #CC3333;     /* RGB(204, 51, 51) */
--period-triassic-dark: #FBBF24;      /* RGB(251, 191, 36) */

--period-jurassic-light: #00B233;     /* RGB(0, 178, 51) */
--period-jurassic-dark: #34D399;      /* RGB(52, 211, 153) */

--period-cretaceous-light: #CC4D99;   /* RGB(204, 77, 153) */
--period-cretaceous-dark: #EC4899;    /* RGB(236, 72, 153) */

--period-paleogene-light: #99334D;    /* RGB(153, 51, 77) */
--period-paleogene-dark: #FCD34D;     /* RGB(252, 211, 77) */

--period-neogene-light: #009933;      /* RGB(0, 153, 51) */
--period-neogene-dark: #10B981;       /* RGB(16, 185, 129) */

--period-quaternary-light: #996633;   /* RGB(153, 102, 51) */
--period-quaternary-dark: #38BDF8;    /* RGB(56, 189, 248) */

--period-unknown-light: #9CA3AF;      /* RGB(156, 163, 175) */
--period-unknown-dark: #6B7280;       /* RGB(107, 114, 128) */

/* Period text (always white for optimal contrast) */
--period-text: #FFFFFF;               /* RGB(255, 255, 255) */
```

### **Form Section Color System**

Color-coded sections for data entry forms:

```css
/* Section identification colors */
--section-basic-light: #22C55E;       /* RGB(34, 197, 94) - Green */
--section-basic-dark: #86EFAC;        /* RGB(134, 239, 172) */

--section-location-light: #F97316;    /* RGB(249, 115, 22) - Orange */
--section-location-dark: #FDBA74;     /* RGB(253, 186, 116) */

--section-value-light: #8B5CF6;       /* RGB(139, 92, 246) - Purple */
--section-value-dark: #C084FC;        /* RGB(192, 132, 252) */

--section-additional-light: #6B7280;  /* RGB(107, 114, 128) - Gray */
--section-additional-dark: #9CA3AF;   /* RGB(156, 163, 175) */
```

---

## ✍️ **Universal Typography System**

### **Typography Philosophy**
The typography system emphasizes readability, scientific precision, and professional presentation while adapting to platform-specific font systems.

### **Font Stack Specification**

#### **Primary Font Stack**
```css
/* Platform-specific font stacks */
--font-family-ios: -apple-system, SF Pro Display, sans-serif;
--font-family-android: Roboto, system-ui, sans-serif;
--font-family-web: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
--font-family-fallback: Arial, Helvetica, sans-serif;
```

### **Typography Scale**

#### **Heading Hierarchy**
```css
/* Main heading levels with exact specifications */
--text-heading-size: 28pt;           /* Primary page titles */
--text-heading-weight: 700;          /* Bold weight */
--text-heading-line-height: 1.2;

--text-subheading-size: 18pt;        /* Section headers */
--text-subheading-weight: 600;       /* Semibold weight */
--text-subheading-line-height: 1.3;

--text-body-size: 16pt;              /* Primary content text */
--text-body-weight: 400;             /* Regular weight */
--text-body-line-height: 1.5;

--text-caption-size: 14pt;           /* Secondary content */
--text-caption-weight: 400;          /* Regular weight */
--text-caption-line-height: 1.4;

--text-small-caption-size: 12pt;     /* Metadata text */
--text-small-caption-weight: 400;    /* Regular weight */
--text-small-caption-line-height: 1.3;

--text-micro-size: 10pt;             /* Fine print */
--text-micro-weight: 400;            /* Regular weight */
--text-micro-line-height: 1.2;
```

#### **Input and Form Typography**
```css
/* Form field typography */
--text-input-label-size: 14pt;       /* Field labels */
--text-input-label-weight: 500;      /* Medium weight */

--text-input-field-size: 16pt;       /* Input content */
--text-input-field-weight: 400;      /* Regular weight */

--text-input-placeholder-size: 16pt; /* Placeholder text */
--text-input-placeholder-weight: 400; /* Regular weight */
--text-input-placeholder-style: italic; /* Placeholder style */
```

#### **Component Typography**
```css
/* Card and component text */
--text-card-title-size: 16pt;        /* Card headers */
--text-card-title-weight: 600;       /* Semibold weight */

--text-card-subtitle-size: 14pt;     /* Card subtitles */
--text-card-subtitle-weight: 400;    /* Regular weight */
--text-card-subtitle-style: italic;  /* Italic style */

--text-section-title-size: 16pt;     /* Section headers */
--text-section-title-weight: 600;    /* Semibold weight */
```

#### **Metadata and Tag Typography**
```css
/* Metadata and specialized text */
--text-metadata-label-size: 13pt;    /* Metadata labels */
--text-metadata-label-weight: 500;   /* Medium weight */

--text-metadata-value-size: 14pt;    /* Metadata values */
--text-metadata-value-weight: 400;   /* Regular weight */

--text-chip-size: 14pt;              /* Filter chip text */
--text-chip-weight: 500;             /* Medium weight */

--text-tag-size: 12pt;               /* Tag text */
--text-tag-weight: 500;              /* Medium weight */

--text-period-tag-size: 10pt;        /* Period tags */
--text-period-tag-weight: 400;       /* Regular weight */
```

### **Typography Usage Guidelines**

#### **Platform Adaptations**
1. **iOS**: Uses SF Pro font family with exact point sizes
2. **Android**: Uses Roboto with sp (scale-independent pixels) units
3. **Web**: Uses system fonts with rem units for scalability

#### **Accessibility Requirements**
- All font sizes support platform dynamic type/font scaling
- Minimum contrast ratios of 4.5:1 for normal text, 3:1 for large text
- Line heights optimized for readability across platforms

---

## 📐 **Universal Layout System**

### **Layout Philosophy**
Consistent spatial relationships and proportions across all platforms while respecting platform-specific design patterns.

### **Spacing Scale**

#### **Base Spacing Units**
```css
/* Primary spacing scale (8pt base unit) */
--spacing-xs: 4pt;    /* 0.5 × base unit */
--spacing-sm: 8pt;    /* 1 × base unit */
--spacing-md: 16pt;   /* 2 × base units */
--spacing-lg: 24pt;   /* 3 × base units */
--spacing-xl: 32pt;   /* 4 × base units */
--spacing-xxl: 48pt;  /* 6 × base units */
```

#### **Component-Specific Spacing**
```css
/* Layout spacing for specific components */
--padding-screen-horizontal: 24pt;   /* Main screen padding */
--padding-card-internal: 16pt;       /* Card content padding */
--padding-input-internal: 12pt;      /* Input field padding */
--padding-chip-horizontal: 10pt;     /* Chip horizontal padding */
--padding-chip-vertical: 6pt;        /* Chip vertical padding */

--margin-section-vertical: 24pt;     /* Section separation */
--margin-component-vertical: 20pt;   /* Component separation */
--margin-element-small: 8pt;         /* Small element margins */
```

### **Border Radius Scale**

#### **Corner Radius Tokens**
```css
/* Border radius scale */
--radius-sm: 8pt;     /* Small radius for minor elements */
--radius-md: 12pt;    /* Medium radius for inputs and icons */
--radius-lg: 16pt;    /* Large radius for cards and major components */
--radius-xl: 20pt;    /* Extra large radius for prominent elements */
--radius-pill: 999pt; /* Pill shape for tags and chips */

/* Component-specific radius */
--radius-card: 16pt;           /* Standard card corners */
--radius-input: 12pt;          /* Input field corners */
--radius-filter-chip: 20pt;    /* Filter chip corners */
--radius-tag-chip: 16pt;       /* Tag chip corners */
--radius-period-tag: 10pt;     /* Small period tag corners */
--radius-icon-container: 12pt; /* Icon background corners */
```

### **Elevation and Shadow System**

#### **Shadow Tokens**
```css
/* Elevation shadows with exact specifications */
--shadow-card: 0 2pt 6pt rgba(0, 0, 0, 0.03);
--shadow-elevated: 0 4pt 12pt rgba(0, 0, 0, 0.08);
--shadow-modal: 0 8pt 24pt rgba(0, 0, 0, 0.15);

/* Component-specific shadows */
--shadow-section-card: 0 2pt 6pt rgba(0, 0, 0, 0.03);
--shadow-authentication: 0 4pt 8pt rgba(0, 0, 0, 0.10);
```

#### **Platform Shadow Adaptations**
- **iOS**: Uses native shadow with blur radius and offset
- **Android**: Translates to elevation values (2dp, 4dp, 8dp)
- **Web**: Uses CSS box-shadow with exact specifications

---

## 🧱 **Universal Component Library**

### **Button System**

#### **Primary Button Specification**
```css
/* Primary button styling */
.button-primary {
  background: linear-gradient(135deg, var(--gradient-primary-start), var(--gradient-primary-end));
  color: #FFFFFF;
  border-radius: var(--radius-lg);
  padding: 16pt var(--padding-screen-horizontal);
  font-size: var(--text-body-size);
  font-weight: 600;
  border: none;
  box-shadow: var(--shadow-card);
  min-height: 44pt; /* Accessibility touch target */
}

.button-primary:disabled {
  background: var(--text-disabled-light);
  color: var(--text-disabled-dark);
  opacity: 0.6;
}

.button-primary:active {
  opacity: 0.8;
  transform: scale(0.98);
}
```

#### **Secondary Button Specification**
```css
/* Secondary button styling */
.button-secondary {
  background: var(--background-secondary-light);
  color: var(--text-primary-light);
  border-radius: var(--radius-lg);
  padding: 16pt var(--padding-screen-horizontal);
  font-size: var(--text-body-size);
  font-weight: 500;
  border: 1pt solid var(--border-light);
  min-height: 44pt;
}

.button-secondary:disabled {
  color: var(--text-disabled-light);
  opacity: 0.6;
}
```

#### **Ghost Button Specification**
```css
/* Ghost button styling */
.button-ghost {
  background: transparent;
  color: var(--accent-blue-light);
  border-radius: var(--radius-md);
  padding: 12pt 16pt;
  font-size: var(--text-caption-size);
  font-weight: 500;
  border: 1pt solid var(--accent-blue-light);
  min-height: 44pt;
}

.button-ghost:active {
  opacity: 0.6;
}
```

### **Input Components**

#### **Text Input Specification**
```css
/* Text input styling */
.input-text {
  background: var(--background-input-light);
  color: var(--text-primary-light);
  border: 1pt solid var(--border-light);
  border-radius: var(--radius-input);
  padding: var(--padding-input-internal);
  font-size: var(--text-input-field-size);
  font-weight: var(--text-input-field-weight);
  min-height: 44pt;
}

.input-text:focus {
  border-color: var(--border-focus);
  box-shadow: 0 0 0 2pt rgba(59, 130, 246, 0.2);
}

.input-text.error {
  border-color: var(--border-error);
  box-shadow: 0 0 0 2pt rgba(239, 68, 68, 0.2);
}

.input-text::placeholder {
  color: var(--text-placeholder-light);
  font-style: var(--text-input-placeholder-style);
}
```

#### **Search Input Specification**
```css
/* Search input styling */
.input-search {
  background: var(--background-input-light);
  border: 1pt solid var(--border-light);
  border-radius: var(--radius-xl);
  padding: var(--spacing-sm) 16pt;
  font-size: var(--text-input-field-size);
  font-weight: var(--text-input-field-weight);
}

.input-search::before {
  content: '🔍';
  color: var(--text-placeholder-light);
  margin-right: var(--spacing-sm);
}
```

### **Card Components**

#### **Standard Card Specification**
```css
/* Standard card styling */
.card {
  background: var(--background-card-light);
  border-radius: var(--radius-card);
  padding: var(--padding-card-internal);
  box-shadow: var(--shadow-card);
  border: 1pt solid var(--border-light);
}

.card-header {
  font-size: var(--text-card-title-size);
  font-weight: var(--text-card-title-weight);
  color: var(--text-primary-light);
  margin-bottom: var(--spacing-sm);
}

.card-subtitle {
  font-size: var(--text-card-subtitle-size);
  font-weight: var(--text-card-subtitle-weight);
  font-style: var(--text-card-subtitle-style);
  color: var(--text-secondary-light);
}
```

#### **Fossil Card Specification**
```css
/* Fossil card styling */
.card-fossil {
  aspect-ratio: 1 / 1;
  background: var(--background-card-light);
  border-radius: var(--radius-card);
  box-shadow: var(--shadow-card);
  overflow: hidden;
  position: relative;
}

.card-fossil-image {
  width: 100%;
  height: 60%;
  object-fit: cover;
  border-radius: var(--radius-card) var(--radius-card) 0 0;
}

.card-fossil-content {
  padding: var(--spacing-md);
  height: 40%;
}

.card-fossil-species {
  font-size: var(--text-card-title-size);
  font-weight: var(--text-card-title-weight);
  color: var(--text-primary-light);
  margin-bottom: var(--spacing-xs);
}

.card-fossil-location {
  font-size: var(--text-card-subtitle-size);
  font-style: italic;
  color: var(--text-secondary-light);
}
```

### **Filter and Tag Components**

#### **Filter Chip Specification**
```css
/* Filter chip styling */
.chip-filter {
  border-radius: var(--radius-filter-chip);
  padding: var(--spacing-sm) 16pt;
  font-size: var(--text-chip-size);
  font-weight: var(--text-chip-weight);
  border: 1pt solid var(--border-light);
  background: transparent;
  color: var(--text-primary-light);
  min-height: 44pt;
  cursor: pointer;
}

.chip-filter.active {
  background: linear-gradient(135deg, var(--gradient-filter-start), var(--gradient-filter-end));
  color: #FFFFFF;
  border-color: transparent;
}

.chip-filter:hover {
  opacity: 0.8;
}
```

#### **Tag Chip Specification**
```css
/* Tag chip styling */
.chip-tag {
  border-radius: var(--radius-tag-chip);
  padding: var(--padding-chip-vertical) var(--padding-chip-horizontal);
  font-size: var(--text-tag-size);
  font-weight: var(--text-tag-weight);
  background: var(--background-secondary-light);
  color: var(--text-primary-light);
  border: 1pt solid var(--border-light);
}
```

#### **Period Tag Specification**
```css
/* Geological period tag styling */
.tag-period {
  border-radius: var(--radius-period-tag);
  padding: 2pt 4pt;
  font-size: var(--text-period-tag-size);
  font-weight: var(--text-period-tag-weight);
  color: var(--period-text);
  /* Background color determined by period */
}

/* Period-specific backgrounds */
.tag-period.precambrian { background: var(--period-precambrian-light); }
.tag-period.cambrian { background: var(--period-cambrian-light); }
.tag-period.ordovician { background: var(--period-ordovician-light); }
/* ... continues for all periods */
```

---

## 🌙 **Theme System Architecture**

### **Theme Implementation Strategy**

#### **Theme Token Structure**
```css
/* Light theme tokens */
:root[data-theme="light"] {
  --text-primary: var(--text-primary-light);
  --text-secondary: var(--text-secondary-light);
  --background-primary: var(--background-primary-light);
  --background-secondary: var(--background-secondary-light);
  /* ... all light theme mappings */
}

/* Dark theme tokens */
:root[data-theme="dark"] {
  --text-primary: var(--text-primary-dark);
  --text-secondary: var(--text-secondary-dark);
  --background-primary: var(--background-primary-dark);
  --background-secondary: var(--background-secondary-dark);
  /* ... all dark theme mappings */
}

/* System theme (automatic) */
@media (prefers-color-scheme: dark) {
  :root[data-theme="system"] {
    --text-primary: var(--text-primary-dark);
    --text-secondary: var(--text-secondary-dark);
    --background-primary: var(--background-primary-dark);
    --background-secondary: var(--background-secondary-dark);
    /* ... dark theme mappings */
  }
}
```

### **Platform Theme Implementations**

#### **iOS Theme Implementation**
```swift
// iOS Theme Manager
class ThemeManager: ObservableObject {
    @AppStorage("selectedTheme") var selectedTheme: Theme = .system
    
    enum Theme: String, CaseIterable {
        case light = "light"
        case dark = "dark"
        case system = "system"
    }
    
    var colorScheme: ColorScheme? {
        switch selectedTheme {
        case .light: return .light
        case .dark: return .dark
        case .system: return nil
        }
    }
}
```

#### **Android Theme Implementation**
```kotlin
// Android Theme Manager
sealed class Theme {
    object Light : Theme()
    object Dark : Theme()
    object System : Theme()
}

class ThemeManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("theme", Context.MODE_PRIVATE)
    
    fun getCurrentTheme(): Theme {
        return when (prefs.getString("theme", "system")) {
            "light" -> Theme.Light
            "dark" -> Theme.Dark
            else -> Theme.System
        }
    }
}
```

#### **Web Theme Implementation**
```javascript
// Web Theme Manager
class ThemeManager {
    constructor() {
        this.theme = localStorage.getItem('theme') || 'system';
        this.applyTheme();
    }
    
    setTheme(theme) {
        this.theme = theme;
        localStorage.setItem('theme', theme);
        this.applyTheme();
    }
    
    applyTheme() {
        document.documentElement.setAttribute('data-theme', this.theme);
    }
}
```

---

## ♿ **Universal Accessibility Specifications**

### **Touch Target Requirements**

#### **Minimum Touch Targets**
```css
/* Accessibility touch target sizes */
--touch-target-minimum: 44pt;        /* iOS/Web minimum */
--touch-target-android: 48dp;        /* Android minimum */
--touch-target-preferred: 48pt;      /* Preferred size across platforms */

/* Component touch target implementations */
.button { min-width: var(--touch-target-minimum); min-height: var(--touch-target-minimum); }
.chip { min-height: var(--touch-target-minimum); }
.input { min-height: var(--touch-target-minimum); }
.card-interactive { min-height: var(--touch-target-minimum); }
```

### **Color Contrast Requirements**

#### **Contrast Ratios**
```css
/* WCAG AA compliance requirements */
--contrast-normal-text: 4.5;         /* Normal text minimum contrast */
--contrast-large-text: 3.0;          /* Large text minimum contrast */
--contrast-interactive: 3.0;         /* Interactive elements minimum */

/* High contrast mode support */
@media (prefers-contrast: high) {
  --text-primary: #000000;
  --background-primary: #FFFFFF;
  --border-light: #000000;
}
```

### **Motion and Animation Preferences**

#### **Reduced Motion Support**
```css
/* Respect reduced motion preferences */
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

/* Standard animation timings */
--animation-fast: 0.15s;
--animation-normal: 0.3s;
--animation-slow: 0.5s;
--animation-easing: cubic-bezier(0.4, 0.0, 0.2, 1);
```

### **Typography Accessibility**

#### **Dynamic Type Support**
```css
/* Platform-specific font scaling */
/* iOS: Supports Dynamic Type automatically */
/* Android: Uses sp units for scaling */
/* Web: Uses rem units with font-size scaling */

--font-scale-small: 0.875;           /* Small text scaling */
--font-scale-normal: 1.0;            /* Normal text scaling */
--font-scale-large: 1.125;           /* Large text scaling */
--font-scale-xl: 1.25;               /* Extra large text scaling */
```

---

## 🔧 **Platform Implementation Guidelines**

### **iOS Implementation**

#### **SwiftUI Integration**
```swift
// Color system integration
extension Color {
    static let textPrimary = Color("textPrimary")
    static let backgroundCard = Color("cardBackground")
    static let gradientStart = Color("gradientStart")
    // ... all design tokens
}

// Typography system integration
extension Font {
    static let heading = Font.system(size: 28, weight: .bold)
    static let subheading = Font.system(size: 18, weight: .semibold)
    static let body = Font.system(size: 16, weight: .regular)
    // ... all typography tokens
}
```

### **Android Implementation**

#### **Material Design Adaptation**
```xml
<!-- colors.xml -->
<resources>
    <color name="text_primary_light">#111827</color>
    <color name="text_primary_dark">#F3F4F6</color>
    <color name="background_primary_light">#FFFFFF</color>
    <color name="background_primary_dark">#121214</color>
    <!-- ... all color tokens -->
</resources>

<!-- dimens.xml -->
<resources>
    <dimen name="spacing_xs">4dp</dimen>
    <dimen name="spacing_sm">8dp</dimen>
    <dimen name="spacing_md">16dp</dimen>
    <dimen name="spacing_lg">24dp</dimen>
    <!-- ... all spacing tokens -->
</resources>
```

#### **Compose Integration**
```kotlin
// Design system theme
object FossilVaultTheme {
    val colors = lightColors(
        primary = Color(0xFF2563EB),
        primaryVariant = Color(0xFF8B5CF6),
        secondary = Color(0xFF34D399),
        background = Color(0xFFFFFFFF),
        surface = Color(0xFFF2F2F2)
    )
    
    val typography = Typography(
        h4 = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
        h6 = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
        body1 = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal)
    )
}
```

### **Web Implementation**

#### **CSS Custom Properties**
```css
/* Root CSS custom properties */
:root {
  /* Color tokens */
  --text-primary: #111827;
  --text-secondary: #6B7280;
  --background-primary: #FFFFFF;
  --background-card: #F2F2F2;
  
  /* Typography tokens */
  --font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  --text-heading-size: 1.75rem;
  --text-subheading-size: 1.125rem;
  --text-body-size: 1rem;
  
  /* Spacing tokens */
  --spacing-xs: 0.25rem;
  --spacing-sm: 0.5rem;
  --spacing-md: 1rem;
  --spacing-lg: 1.5rem;
  
  /* Border radius tokens */
  --radius-sm: 0.5rem;
  --radius-md: 0.75rem;
  --radius-lg: 1rem;
}
```

#### **React Component Integration**
```tsx
// Design system hook
export const useTheme = () => {
  const [theme, setTheme] = useState<'light' | 'dark' | 'system'>('system');
  
  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);
  
  return { theme, setTheme };
};

// Styled components with design tokens
const Card = styled.div`
  background: var(--background-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-md);
  box-shadow: var(--shadow-card);
`;
```

---

## 📱 **Responsive Design System**

### **Breakpoint System**

#### **Universal Breakpoints**
```css
/* Responsive breakpoints for web */
--breakpoint-xs: 320px;      /* Small phones */
--breakpoint-sm: 375px;      /* Large phones */
--breakpoint-md: 768px;      /* Tablets */
--breakpoint-lg: 1024px;     /* Small desktops */
--breakpoint-xl: 1440px;     /* Large desktops */

/* iOS device breakpoints */
--ios-se: 320px;             /* iPhone SE */
--ios-standard: 375px;       /* iPhone 12/13/14 */
--ios-plus: 414px;           /* iPhone Plus models */
--ios-pro-max: 428px;        /* iPhone Pro Max */
--ios-ipad: 768px;           /* iPad */
--ios-ipad-pro: 1024px;      /* iPad Pro */

/* Android device breakpoints */
--android-small: 320px;      /* Small Android phones */
--android-medium: 360px;     /* Medium Android phones */
--android-large: 411px;      /* Large Android phones */
--android-tablet: 768px;     /* Android tablets */
```

### **Layout Adaptations**

#### **Grid System**
```css
/* Responsive grid system */
.grid-container {
  display: grid;
  gap: var(--spacing-md);
  padding: var(--padding-screen-horizontal);
}

/* Fossil card grid */
.grid-fossils {
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
}

@media (min-width: 768px) {
  .grid-fossils {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
}

@media (min-width: 1024px) {
  .grid-fossils {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}
```

#### **Component Scaling**
```css
/* Component responsive behavior */
.component-responsive {
  /* Mobile first approach */
  padding: var(--spacing-md);
  font-size: var(--text-body-size);
}

@media (min-width: 768px) {
  .component-responsive {
    padding: var(--spacing-lg);
    font-size: calc(var(--text-body-size) * 1.1);
  }
}
```

---

## 🔧 **Design System Maintenance**

### **Token Management**

#### **Design Token Structure**
```json
{
  "color": {
    "text": {
      "primary": {
        "light": "#111827",
        "dark": "#F3F4F6"
      },
      "secondary": {
        "light": "#6B7280",
        "dark": "#D1D5DB"
      }
    },
    "background": {
      "primary": {
        "light": "#FFFFFF",
        "dark": "#121214"
      }
    },
    "period": {
      "jurassic": {
        "light": "#00B233",
        "dark": "#34D399"
      }
    }
  },
  "typography": {
    "heading": {
      "size": "28pt",
      "weight": "700",
      "lineHeight": "1.2"
    }
  },
  "spacing": {
    "xs": "4pt",
    "sm": "8pt",
    "md": "16pt",
    "lg": "24pt"
  }
}
```

### **Version Control Strategy**

#### **Semantic Versioning**
- **Major (1.0.0)**: Breaking changes to design tokens or component APIs
- **Minor (1.1.0)**: New components or non-breaking enhancements
- **Patch (1.1.1)**: Bug fixes, small adjustments, accessibility improvements

#### **Cross-Platform Synchronization**
1. **Design Token Updates**: Propagate automatically across all platforms
2. **Component Library**: Maintain feature parity across platforms
3. **Documentation**: Keep platform-specific implementation guides updated
4. **Testing**: Validate design consistency across all implementations

### **Quality Assurance**

#### **Design System Checklist**
- [ ] Color contrast ratios meet WCAG AA standards
- [ ] Typography scales appropriately across all platforms
- [ ] Touch targets meet minimum size requirements
- [ ] Components work in both light and dark themes
- [ ] Geological period colors maintain scientific accuracy
- [ ] Platform adaptations respect native design patterns
- [ ] Accessibility features work across all platforms
- [ ] Design tokens are properly synchronized
- [ ] Documentation is complete and up-to-date
- [ ] Cross-platform visual consistency verified

This platform-agnostic design system ensures Fossil Vault maintains consistent visual identity, accessibility standards, and user experience quality across iOS, Android, and Web implementations while respecting each platform's unique design language and technical constraints.