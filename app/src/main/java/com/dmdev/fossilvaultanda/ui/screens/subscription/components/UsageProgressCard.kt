package com.dmdev.fossilvaultanda.ui.screens.subscription.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Card displaying usage progress with animated progress bar.
 * Shows current usage vs limit with visual progress indicator.
 */
@Composable
fun UsageProgressCard(
    title: String,
    current: Int,
    limit: Int?,
    formatValue: (Int) -> String = { it.toString() },
    modifier: Modifier = Modifier
) {
    val progress = if (limit != null && limit > 0) {
        (current.toFloat() / limit.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val isNearLimit = limit != null && progress >= 0.8f
    val isAtLimit = limit != null && current >= limit

    // Animated progress with spring animation
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress_animation"
    )

    val statusText = when {
        limit == null -> "Unlimited"
        isAtLimit -> "Limit reached"
        isNearLimit -> "Nearly at limit"
        else -> "${formatValue(limit - current)} remaining"
    }

    val progressColor = when {
        isAtLimit -> MaterialTheme.colorScheme.error
        isNearLimit -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Current / Limit display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (limit != null) {
                        "${formatValue(current)} / ${formatValue(limit)}"
                    } else {
                        "${formatValue(current)} / Unlimited"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isNearLimit || isAtLimit) progressColor
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            if (limit != null) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            }
        }
    }
}

/**
 * Card displaying storage usage progress with human-readable sizes.
 */
@Composable
fun StorageUsageProgressCard(
    current: Long,
    limit: Long,
    modifier: Modifier = Modifier
) {
    UsageProgressCard(
        title = "Storage Used",
        current = current.toInt(),
        limit = limit.toInt(),
        formatValue = { bytes ->
            formatStorageSize(bytes.toLong())
        },
        modifier = modifier
    )
}

/**
 * Formats bytes to human-readable storage size (MB, GB).
 */
private fun formatStorageSize(bytes: Long): String {
    return when {
        bytes < 1024 * 1024 -> {
            val kb = bytes / 1024.0
            "%.1f KB".format(kb)
        }
        bytes < 1024 * 1024 * 1024 -> {
            val mb = bytes / (1024.0 * 1024.0)
            "%.1f MB".format(mb)
        }
        else -> {
            val gb = bytes / (1024.0 * 1024.0 * 1024.0)
            "%.2f GB".format(gb)
        }
    }
}
