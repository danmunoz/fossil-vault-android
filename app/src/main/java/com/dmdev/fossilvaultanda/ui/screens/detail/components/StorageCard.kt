package com.dmdev.fossilvaultanda.ui.screens.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.Specimen

/**
 * Card displaying storage location information for a specimen.
 * Shows room, cabinet, and drawer information when available.
 * Matches iOS StorageCard implementation.
 */
@Composable
fun StorageCard(
    specimen: Specimen,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = "Storage location information"
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Section header
            ListItem(
                headlineContent = {
                    Text(
                        text = "Storage",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = Color.Cyan
                    )
                }
            )

            HorizontalDivider()

            // Storage information
            specimen.storage?.let { storage ->
                // Drawer
                storage.drawer?.let { drawer ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = drawer,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = "Drawer",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Drawer: $drawer"
                        }
                    )
                }

                // Cabinet
                storage.cabinet?.let { cabinet ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = cabinet,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = "Cabinet",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Cabinet: $cabinet"
                        }
                    )
                }

                // Room
                storage.room?.let { room ->
                    ListItem(
                        headlineContent = {
                            Text(
                                text = room,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        supportingContent = {
                            Text(
                                text = "Room",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Room: $room"
                        }
                    )
                }
            }
        }
    }
}
