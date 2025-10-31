package com.dmdev.fossilvaultanda.ui.screens.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.OwnershipStatus
import com.dmdev.fossilvaultanda.data.models.Specimen

/**
 * Banner displayed at the top of detail screen when specimen is archived.
 * Shows archive status with color coding based on disposition type.
 * Matches iOS archive banner implementation.
 */
@Composable
fun ArchiveBanner(
    specimen: Specimen,
    modifier: Modifier = Modifier
) {
    val disposition = specimen.disposition ?: return
    val statusColor = getStatusColor(disposition.status)
    val statusText = getStatusText(disposition.status)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = "This specimen has been archived. Status: $statusText"
            },
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Archive,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Archived",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Text(
                    text = "This specimen has been archived. ($statusText)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun getStatusColor(status: OwnershipStatus): Color {
    return when (status) {
        OwnershipStatus.SOLD -> Color(0xFF4CAF50) // Green
        OwnershipStatus.TRADED -> Color(0xFF2196F3) // Blue
        OwnershipStatus.GIFTED -> Color(0xFF9C27B0) // Purple
        OwnershipStatus.LOST -> Color(0xFFFF9800) // Orange
    }
}

private fun getStatusText(status: OwnershipStatus): String {
    return when (status) {
        OwnershipStatus.SOLD -> "Sold"
        OwnershipStatus.TRADED -> "Traded"
        OwnershipStatus.GIFTED -> "Gifted"
        OwnershipStatus.LOST -> "Lost"
    }
}
