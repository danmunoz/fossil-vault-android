# Platform-Agnostic HomeView Reference

## Overview
The HomeView is the primary interface for displaying and managing a user's fossil specimen collection. It provides comprehensive functionality for viewing, searching, filtering, and organizing specimens across different display modes.

## Core Architecture Concepts

### Data Dependencies
- **Specimen Data Provider**: Reactive data source for specimen collection
- **Authentication Manager**: User authentication state and management
- **Subscription Manager**: Premium feature access and usage limits

### State Management
- **Search State**: Current search query and active filters
- **Display Preferences**: View mode (grid/list) and sort options
- **Selection State**: Currently selected items for actions
- **Navigation State**: Modal presentations and deep linking

## Primary Features

### 1. Navigation Structure
**Header Area**:
- **Primary Action**: Add new specimen (respects subscription limits)
- **Secondary Actions**: 
  - Statistics/Analytics view access
  - Settings/Profile access
- **Title Area**: App branding or collection name

### 2. Search & Discovery
**Search Functionality**:
- **Query Input**: Text-based search across specimen names/species
- **Placeholder**: "Search your collection"
- **Behavior**: Real-time filtering with case-insensitive matching

**Filter System**:
- **Sort Options**: Recent, Oldest, Name A-Z, Name Z-A
- **Period Filtering**: Geological time period selection (optional)
- **Result Feedback**: Display count of filtered results

### 3. Display Modes

#### Grid Layout
**Structure**:
- Multi-column grid (typically 2 columns on mobile)
- Square or rectangular cards with consistent aspect ratios
- Responsive spacing between items

**Content Per Card**:
- **Primary Image**: Specimen photograph (fallback to placeholder icon)
- **Species Name**: Primary identifier (truncated if necessary)
- **Location Info**: Geographic location with location indicator (if available)
- **Period Tag**: Geological time period with distinctive styling
- **Favorite Indicator**: Visual marker for favorited items

#### List Layout
**Structure**:
- Single-column vertical list
- Thumbnail + metadata layout
- Higher information density than grid

**Content Per Row**:
- **Thumbnail**: Small specimen image (consistent size)
- **Primary Text**: Species name
- **Secondary Text**: Period, element type, location
- **Status Indicators**: Favorite status, other metadata

### 4. Item Interaction Patterns

#### Primary Actions
- **Tap/Click**: Navigate to detailed specimen view
- **Search**: Filter collection in real-time

#### Secondary Actions (Context-Dependent)
- **View Details**: Navigate to full specimen information
- **Edit**: Modify specimen data
- **Share**: Export or share specimen information
- **Delete**: Remove specimen with confirmation
- **Favorite Toggle**: Mark/unmark as favorite

#### Platform-Specific Interaction Patterns
*Implementation varies by platform (long-press menus, overflow menus, selection modes, etc.)*

### 5. Empty States
**No Specimens**: 
- **Visual**: Empty state illustration/icon
- **Message**: Encouraging text about adding first specimen
- **Action**: Direct path to add specimen (respects limits)

**No Search Results**:
- **Visual**: Search-specific empty state
- **Message**: "No specimens match your search"
- **Action**: Clear search or modify filters

### 6. Modal Presentations

#### Settings/Profile Interface
- User preferences and account management
- Subscription status and upgrade options
- Data export and management tools

#### Specimen Management
- **Add New**: Full specimen entry form
- **Edit Existing**: Pre-populated form for modifications
- **Limit Reached**: Subscription upgrade prompts

#### Sharing Interface
- Specimen data export options
- Social sharing capabilities
- Format selection for different platforms

### 7. Data Flow Logic

#### Filtering Pipeline
1. **Base Collection**: All user specimens
2. **Search Filter**: Text matching against species names
3. **Period Filter**: Geological time period selection
4. **Sort Application**: Order by selected criteria
5. **Result Presentation**: Formatted for current display mode

#### Sort Algorithms
- **Recent**: Creation date descending (newest first)
- **Oldest**: Creation date ascending (oldest first)
- **Name A-Z**: Alphabetical by species name ascending
- **Name Z-A**: Alphabetical by species name descending

### 8. Business Logic Rules

#### Subscription Limits
- **Free Tier**: Limited number of specimens
- **Premium Tier**: Unlimited or higher limits
- **Limit Enforcement**: Block creation when limit reached
- **Upgrade Flow**: Direct path to subscription management

#### Authentication States
- **Authenticated**: Full sync and cloud features
- **Local Only**: Device-only storage and functionality
- **Guest Mode**: Limited features with upgrade paths

## Design System Requirements

### Visual Hierarchy
- **Primary Content**: Specimen images and names (largest visual weight)
- **Secondary Content**: Metadata like location and period (medium weight)
- **Tertiary Content**: Action buttons and system UI (minimal weight)

### Typography Scale
- **Primary Titles**: Species names - medium to large, medium weight
- **Secondary Text**: Metadata and labels - small to medium, regular weight
- **Interface Text**: Buttons and system text - small, medium weight
- **Tags/Chips**: Period indicators - small, medium weight

### Color System
- **Content Colors**: High contrast text on appropriate backgrounds
- **Interactive Elements**: Consistent color for buttons and links
- **Status Indicators**: Distinct colors for favorites, periods, etc.
- **Background Hierarchy**: Clear distinction between surfaces

### Spacing & Layout
- **Content Spacing**: Consistent margins and padding throughout
- **Grid Spacing**: Uniform gaps between grid items
- **List Spacing**: Appropriate vertical rhythm in list mode
- **Touch Targets**: Minimum size requirements for interactive elements

## Performance Considerations

### Data Loading
- **Lazy Loading**: Load visible items first
- **Image Optimization**: Appropriate resolution and caching
- **Background Updates**: Non-blocking data refresh
- **Memory Management**: Efficient handling of large collections

### User Interface
- **Smooth Scrolling**: Optimized for large datasets
- **Responsive Filtering**: Real-time search without lag
- **Animation Performance**: Smooth transitions between states
- **State Preservation**: Maintain user context across navigation

## Accessibility Requirements

### Screen Reader Support
- **Semantic Labels**: Meaningful descriptions for all interactive elements
- **Content Description**: Image alt text and card summaries
- **Navigation Hints**: Clear indication of available actions
- **State Announcements**: Changes in filter/sort state

### Visual Accessibility
- **Color Independence**: Information not conveyed by color alone
- **Contrast Ratios**: Text meets minimum contrast requirements
- **Font Scaling**: Support for user font size preferences
- **Focus Indicators**: Clear visual focus for keyboard navigation

### Motor Accessibility
- **Touch Target Size**: Minimum 44px/44dp for interactive elements
- **Gesture Alternatives**: Alternative methods for complex gestures
- **Error Prevention**: Confirmation for destructive actions

## Platform Adaptation Guidelines

### Respect Platform Conventions
- **Navigation Patterns**: Use platform-standard navigation models
- **Interaction Paradigms**: Leverage familiar user interaction patterns
- **Visual Design**: Align with platform design language
- **System Integration**: Utilize platform-specific capabilities

### Optimize for Platform Strengths
- **Input Methods**: Optimize for platform-specific input (touch, mouse, keyboard)
- **Screen Sizes**: Responsive design for platform screen variations
- **Hardware Features**: Leverage available sensors and capabilities
- **System Features**: Integrate with platform-specific sharing, notifications, etc.

This platform-agnostic reference provides the conceptual foundation for implementing the HomeView across different platforms while maintaining consistent functionality and user experience principles.