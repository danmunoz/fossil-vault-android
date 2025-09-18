package com.dmdev.fossilvaultanda.ui.screens.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.data.models.Taxonomy
import com.dmdev.fossilvaultanda.data.models.enums.AcquisitionMethod
import com.dmdev.fossilvaultanda.data.models.enums.Condition
import com.dmdev.fossilvaultanda.data.models.enums.FossilElement
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme
import kotlinx.datetime.Clock

@Composable
fun AcquisitionCard(
    specimen: Specimen,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = "Acquisition information for specimen"
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Text(
                text = "Acquisition Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Acquisition Method
            if (specimen.acquisitionMethod != null) {
                AcquisitionInfoRow(
                    icon = Icons.Default.ShoppingCart,
                    label = "Method",
                    value = specimen.acquisitionMethod.displayString,
                    contentDescription = "Acquisition method: ${specimen.acquisitionMethod.displayString}"
                )
            }

            // Collection Date
            if (specimen.collectionDate != null) {
                AcquisitionInfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Collection Date",
                    value = specimen.collectionDate.toString().split("T")[0], // Simple date formatting
                    contentDescription = "Collection date: ${specimen.collectionDate}"
                )
            }

            // Acquisition Date
            if (specimen.acquisitionDate != null) {
                AcquisitionInfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Acquisition Date",
                    value = specimen.acquisitionDate.toString().split("T")[0], // Simple date formatting
                    contentDescription = "Acquisition date: ${specimen.acquisitionDate}"
                )
            }

            // Show empty state if no acquisition info
            if (specimen.acquisitionMethod == null &&
                specimen.collectionDate == null &&
                specimen.acquisitionDate == null) {
                Text(
                    text = "No acquisition information available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.semantics {
                        contentDescription = "No acquisition information available"
                    }
                )
            }
        }
    }
}

@Composable
private fun AcquisitionInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics {
                this.contentDescription = contentDescription
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AcquisitionCardPreview() {
    FossilVaultTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // With acquisition info
            AcquisitionCard(
                specimen = Specimen(
                    id = "1",
                    userId = "user1",
                    taxonomy = Taxonomy.fromSpeciesString("Tyrannosaurus rex"),
                    element = FossilElement.TOOTH,
                    acquisitionMethod = AcquisitionMethod.PURCHASED,
                    condition = Condition.Natural,
                    acquisitionDate = Clock.System.now(),
                    creationDate = Clock.System.now()
                )
            )

            // Without acquisition info
            AcquisitionCard(
                specimen = Specimen(
                    id = "2",
                    userId = "user1",
                    taxonomy = Taxonomy.fromSpeciesString("Triceratops horridus"),
                    element = FossilElement.HORN,
                    creationDate = Clock.System.now()
                )
            )
        }
    }
}