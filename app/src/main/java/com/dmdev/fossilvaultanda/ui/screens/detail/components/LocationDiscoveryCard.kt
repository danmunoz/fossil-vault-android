package com.dmdev.fossilvaultanda.ui.screens.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.Specimen

@Composable
fun LocationDiscoveryCard(
    specimen: Specimen,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = "Location and discovery information"
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
                        text = "Location & Discovery",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
            
            HorizontalDivider()
            
            // Location information
            specimen.location?.let { location ->
                ListItem(
                    headlineContent = { 
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    supportingContent = { 
                        Text(
                            text = "Found at",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.Place, 
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Location: $location"
                    }
                )
            }
            
            // Formation information
            specimen.formation?.let { formation ->
                ListItem(
                    headlineContent = { 
                        Text(
                            text = formation,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    supportingContent = { 
                        Text(
                            text = "Formation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.Map, 
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Formation: $formation"
                    }
                )
            }
            
            // Coordinates with map integration
            if (specimen.latitude != null && specimen.longitude != null) {
                val coordinatesText = "${String.format("%.6f", specimen.latitude)}, ${String.format("%.6f", specimen.longitude)}"
                
                ListItem(
                    headlineContent = { 
                        Text(
                            text = coordinatesText,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    supportingContent = { 
                        Text(
                            text = "Coordinates",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.Map, 
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = onMapClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.OpenInNew, 
                                contentDescription = "Open in Maps",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Coordinates: $coordinatesText, tap to open in maps"
                    }
                )
            }
            
        }
    }
}