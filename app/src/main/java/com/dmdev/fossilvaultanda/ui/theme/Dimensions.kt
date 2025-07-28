package com.dmdev.fossilvaultanda.ui.theme

import androidx.compose.ui.unit.dp

// FossilVault Spacing System (8dp base unit)
object FossilVaultSpacing {
    val xs = 4.dp    // 0.5 × base unit
    val sm = 8.dp    // 1 × base unit
    val md = 16.dp   // 2 × base units
    val lg = 24.dp   // 3 × base units
    val xl = 32.dp   // 4 × base units
    val xxl = 48.dp  // 6 × base units
    
    // Component-specific spacing
    val screenHorizontal = 24.dp  // Main screen padding
    val cardInternal = 16.dp      // Card content padding
    val inputInternal = 12.dp     // Input field padding
    val chipHorizontal = 10.dp    // Chip horizontal padding
    val chipVertical = 6.dp       // Chip vertical padding
    val sectionVertical = 24.dp   // Section separation
    val componentVertical = 20.dp // Component separation
}

// FossilVault Border Radius System
object FossilVaultRadius {
    val sm = 8.dp     // Small radius for minor elements
    val md = 12.dp    // Medium radius for inputs and icons
    val lg = 16.dp    // Large radius for cards and major components
    val xl = 20.dp    // Extra large radius for prominent elements
    val pill = 999.dp // Pill shape for tags and chips
    
    // Component-specific radius
    val card = 16.dp          // Standard card corners
    val input = 12.dp         // Input field corners
    val filterChip = 20.dp    // Filter chip corners
    val tagChip = 16.dp       // Tag chip corners
    val periodTag = 10.dp     // Small period tag corners
    val iconContainer = 12.dp // Icon background corners
}

// Touch target sizes for accessibility
object FossilVaultTouchTarget {
    val minimum = 44.dp    // Minimum touch target
    val preferred = 48.dp  // Preferred touch target
}

// Animation durations
object FossilVaultAnimations {
    const val fast = 150
    const val normal = 300
    const val slow = 500
}

// Alias for backward compatibility and cleaner code
object Dimensions {
    val small = FossilVaultSpacing.sm
    val medium = FossilVaultSpacing.md
    val large = FossilVaultSpacing.lg
    val extraLarge = FossilVaultSpacing.xl
    val xxLarge = FossilVaultSpacing.xxl
}