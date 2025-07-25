# Fossil Detail View - Platform Agnostic UX Specification

## Overview
The Fossil Detail View is a comprehensive information display screen that presents detailed metadata about a fossil specimen in an organized, visually appealing manner. This view serves as the primary interface for users to examine all aspects of their collected fossils.

## Core Functionality

### Primary Purpose
- Display comprehensive fossil specimen information
- Enable quick access to editing and sharing capabilities
- Provide visual representation through image gallery
- Allow contextual actions (edit, share, export, delete)

### User Journey
1. User navigates to view from collection list or search results
2. Immediately sees hero image(s) in prominent gallery
3. Scrolls through organized information cards
4. Can access contextual actions via overflow menu
5. Can navigate to full-screen image viewer
6. Can access editing mode or sharing functionality

## Visual Hierarchy & Layout

### Top Level Structure
1. **Navigation Header** - Title, back navigation, overflow menu
2. **Image Gallery** - Hero visual content with pagination
3. **Information Cards** - Organized data sections with conditional visibility
4. **Action Sheets** - Context-dependent actions (edit, share, export, delete)

### Information Architecture
The content is organized into thematic cards that appear conditionally based on available data:

#### 1. Species & Classification Card (Always visible)
- **Hero Content**: Species name (large, prominent typography)
- **Secondary**: Element/type information
- **Metadata**: Geological period with color-coded badge
- **Tags**: Flexible tag collection using flow layout

#### 2. Location & Discovery Card (Conditional)
- **Location Information**: Found location, formation details
- **Coordinates**: GPS data with interactive map access
- **Timeline**: Collection and acquisition dates with iconography

#### 3. Physical Properties Card (Conditional)
- **Dimension Summary**: Combined size description
- **Individual Measurements**: Width, height, length with appropriate units
- **Visual Indicators**: Directional icons for each dimension

#### 4. Value & Inventory Card (Conditional)
- **Financial Data**: Purchase price and estimated value
- **Value Analysis**: Appreciation/depreciation calculation and visualization
- **Inventory**: System ID, creation date, notes

## Interaction Patterns

### Navigation
- **Back Navigation**: Single gesture to return to previous screen
- **Breadcrumb Context**: Clear indication of current location in app hierarchy

### Image Interaction
- **Gallery Navigation**: Horizontal swipe/pagination between multiple images
- **Full-Screen Viewing**: Tap-to-expand for detailed image examination
- **Image Selection**: Current image indicator for sharing/export contexts

### Action Discovery
- **Overflow Menu**: Three-dot menu revealing contextual actions
- **Action Sheets**: Platform-appropriate modal for action selection
- **Confirmation Dialogs**: Destructive actions require explicit confirmation

### Data Interaction
- **Coordinates**: Tappable to open map view
- **Copyable Content**: Long-press or button to copy inventory IDs
- **Interactive Elements**: Clear visual affordances for tappable content

## Content Display Principles

### Conditional Visibility
- Cards only appear when relevant data exists
- Empty states avoided through smart conditional rendering
- Progressive disclosure prevents overwhelming users

### Information Density
- **Scannable Layout**: Clear visual hierarchy with icons, labels, and values
- **Grouped Content**: Related information clustered within themed cards
- **Breathing Room**: Adequate spacing between sections and elements

### Visual Design Language
- **Color Coding**: Semantic use of color (geological periods, value indicators)
- **Iconography**: Consistent icon system for categorization and actions
- **Typography**: Clear hierarchy with emphasis on species name as hero content
- **Card Design**: Elevated surfaces with consistent shadow and corner radius

## Responsive Behavior

### Content Adaptation
- **Text Wrapping**: Graceful handling of long species names and descriptions
- **Image Scaling**: Aspect-ratio aware image display
- **Card Stacking**: Vertical arrangement with consistent spacing

### Interactive States
- **Loading States**: Progressive image loading with placeholders
- **Error Handling**: Graceful degradation for missing images or data
- **Touch Feedback**: Clear visual response to user interactions

## Accessibility Considerations

### Content Structure
- **Semantic Organization**: Logical reading order and content hierarchy
- **Descriptive Labels**: Clear, descriptive text for all interactive elements
- **Alternative Text**: Image descriptions for visual content

### Interaction Support
- **Focus Management**: Logical tab order and focus indicators
- **Touch Targets**: Appropriately sized interactive areas
- **Voice Control**: Support for voice navigation and commands

## Platform Implementation Notes

### Cross-Platform Consistency
- **Core Information Architecture**: Maintain consistent card organization
- **Essential Interactions**: Preserve key user flows and action patterns
- **Visual Identity**: Adapt design language to platform conventions while maintaining brand

### Platform-Specific Adaptations
- **Navigation Patterns**: Use platform-native back navigation and menu systems
- **Action Sheets**: Implement using platform-appropriate modal presentations
- **Typography**: Adapt to platform typography scales and reading patterns
- **Touch Interactions**: Follow platform gesture conventions and feedback patterns

## Success Metrics

### User Experience Goals
- **Information Discoverability**: Users can quickly find relevant specimen data
- **Action Efficiency**: Common tasks (edit, share) are easily accessible
- **Visual Comprehension**: Complex data is presented in digestible format
- **Navigation Clarity**: Users maintain context and can easily return to collection

This specification serves as the foundation for implementing the Fossil Detail View across different platforms while maintaining consistent user experience and information architecture.