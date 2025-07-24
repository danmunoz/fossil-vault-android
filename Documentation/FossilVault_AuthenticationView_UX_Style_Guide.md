# FossilVault Authentication View - UX & Style Guide

## Overview

This document provides comprehensive specifications for the FossilVault authentication interface, designed to be platform-agnostic and replicable across iOS, Android, Web, and other platforms. The authentication view supports both **Sign In** and **Sign Up** modes with seamless transitions between them.

## Visual Design

### Logo & Branding
- **App Icon**: Orange diamond-shaped icon with white diamond in center
- **App Name**: "FossilVault" (no spacing)
- **Tagline**: "Your Personal Fossil Collection, Anywhere"
- **Logo Animation**: Implemented with rotating/scaling animation effects
- **Logo Scale**: 60% of original size (0.6 scale factor)
- **Logo Container Height**: 120pt

### Color Palette

#### Primary Colors
- **Background**: Light cream/off-white (`background`)
- **Primary Text**: Near-black (`textPrimary`)
- **Secondary Text**: Medium gray (`textGray`)
- **Primary Blue**: Blue accent for interactive elements (`iconBlue`)

#### Authentication-Specific Colors
- **Button Gradient**: Blue to purple gradient (`gradientStart` to `gradientEnd`)
- **Shadow Color**: Subtle orange shadow for title (`authenticationShadow`)
- **Input Background**: Light gray (`inputBackground`)
- **Border Color**: Light gray (`border`)

#### Validation Colors
- **Error Red**: `#EF4444`
- **Password Strength Colors**:
  - Weak: Red
  - Fair: Orange
  - Good: Yellow
  - Strong: Green

### Typography

#### Hierarchy
- **Main Title**: 28pt, Bold weight
- **Subtitle**: 16pt, Regular weight
- **Field Labels**: 14pt, Medium weight
- **Input Text**: 16pt, Regular weight
- **Button Text**: System default, Medium weight
- **Caption Text**: 14pt, Regular weight
- **Small Caption**: 12pt, Regular weight

#### Font Treatment
- **Title Shadow**: Subtle shadow with orange tint and 5pt radius
- **Multiline Text**: Center-aligned for titles and subtitles
- **Field Labels**: Left-aligned, consistent spacing

## Layout & Spacing

### Container Structure
- **Main Container**: ScrollView with full-screen coverage
- **Content Padding**: 24pt horizontal margins
- **Top Padding**: 40pt from top
- **Close Button**: Top-right corner with 8pt padding

### Vertical Spacing
- **Section Spacing**: 32pt between major sections
- **Field Spacing**: 20pt between form fields
- **Button Spacing**: 16pt between primary and secondary actions
- **Internal Spacing**: 8pt for related elements

### Component Sizing
- **Input Fields**: Full width with 12pt internal padding
- **Buttons**: Full width with standard height
- **Corner Radius**: 12pt for input fields and buttons

## User Experience Patterns

### Mode Switching
- **Default Mode**: Sign Up
- **Toggle States**: "Already have an account? Sign in" / "Don't have an account? Create one"
- **Transition**: Smooth animated transition between modes
- **Content Persistence**: Email and password values persist during mode switch
- **Focus Management**: Clear focus during transitions

### Form Validation

#### Real-time Validation
- **Email Validation**: Must contain "@" symbol and not be empty
- **Password Requirements**: Minimum 6 characters
- **Form State**: Enable/disable submit button based on validation
- **Error Handling**: Display error messages below relevant fields

#### Password Strength (Sign Up Only)
- **Visual Indicator**: Horizontal progress bar
- **Strength Levels**: Weak (25%), Fair (50%), Good (75%), Strong (100%)
- **Criteria**:
  - **Strong**: 8+ chars, numbers, uppercase, special characters
  - **Good**: 6+ chars, numbers, uppercase OR special characters
  - **Fair**: 6+ chars, numbers OR uppercase
  - **Weak**: Less than above criteria
- **Animation**: Smooth progress bar animation with spring physics

### Interactive States

#### Loading States
- **Button Loading**: Replace text with circular progress indicator
- **Button Scale**: Subtle scale down (0.98) during loading
- **Form Disabled**: All inputs disabled during authentication

#### Focus Management
- **Tab Order**: Email → Password → Submit
- **Auto-advance**: Email submit advances to password field
- **Keyboard Handling**: Email keyboard type, secure entry for password

### Error Handling
- **Error Display**: Red text below form fields
- **Error Styling**: Increased field border width and red color
- **Field Animation**: Subtle scale effect (1.02) for error state
- **Error Clearance**: Errors clear when user starts typing

## Content Strategy

### Messaging Hierarchy

#### Sign Up Mode
- **Title**: "Let's Get You Started"
- **Subtitle**: "Join FossilVault and start building your fossil collection"
- **Primary Button**: "Create Account" with person.badge.plus icon
- **Switch Prompt**: "Already have an account? Sign in"

#### Sign In Mode
- **Title**: "Welcome back!"
- **Subtitle**: "Sign in to access your collection and continue exploring"
- **Primary Button**: "Sign In" with arrow.right.circle.fill icon
- **Switch Prompt**: "Don't have an account? Create one"

### Accessibility & Localization
- **String Keys**: All text uses LocalizedStringKey for internationalization
- **Focus Management**: Proper focus state handling for screen readers
- **Button Icons**: Descriptive SF Symbols for visual context
- **Error Messages**: Clear, user-friendly error descriptions

## Animation & Motion

### Transition Effects
- **Mode Switch**: Spring animation (0.6 response, 0.8 damping)
- **Content Staggering**: Delayed appearance of form sections (0.1s, 0.3s, 0.6s delays)
- **Password Strength**: Spring animation for progress bar updates
- **Button Interactions**: Spring animation for loading states

### Performance Considerations
- **Smooth Transitions**: Hardware-accelerated animations
- **Reduced Motion**: Respect system accessibility preferences
- **Memory Management**: Proper cleanup of animation states

## Platform Implementation Notes

### iOS Specific
- **SwiftUI Framework**: Native implementation with EnvironmentObject patterns
- **Keyboard Handling**: Automatic keyboard avoidance
- **Safe Area**: Respect device safe areas

### Cross-Platform Considerations
- **Design Tokens**: All styling values extracted to design system
- **Component Architecture**: Modular, reusable components
- **State Management**: Observable patterns for reactive updates
- **Responsive Design**: Adaptable to different screen sizes

## Technical Specifications

### Component Architecture
```
AuthenticationView
├── Header Section (Logo + Titles)
├── Form Section (Email + Password)
├── Action Section (Buttons)
└── Mode Switch Section
```

### State Management
- **Authentication Mode**: Toggle between login/signUp
- **Form State**: Email, password, loading, error states
- **Focus State**: Active input field tracking
- **Animation State**: Transition and loading animations

### Input Specifications
- **Email Field**: emailAddress keyboard, username content type
- **Password Field**: secure entry, password content type
- **Submit Labels**: "next" for email, "join" for password

This specification ensures consistent implementation across platforms while maintaining the refined, user-friendly experience of the original FossilVault authentication interface.