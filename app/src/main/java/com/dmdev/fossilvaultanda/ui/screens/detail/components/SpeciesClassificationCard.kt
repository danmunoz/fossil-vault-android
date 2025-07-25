package com.dmdev.fossilvaultanda.ui.screens.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.ui.theme.getPeriodColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpeciesClassificationCard(
    specimen: Specimen,
    onPeriodClick: () -> Unit,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics {
                contentDescription = "Species and classification information for ${specimen.species}"
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row with species name and favorite indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Species name (hero content)
                    Text(
                        text = specimen.species,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.semantics {
                            contentDescription = "Species: ${specimen.species}"
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Element/type information
                    Text(
                        text = specimen.element.displayString,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.semantics {
                            contentDescription = "Element type: ${specimen.element.displayString}"
                        }
                    )
                }
                
                // Favorite indicator
                if (specimen.isFavorite) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Marked as favorite",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Geological period chip
            FilterChip(
                selected = false,
                onClick = onPeriodClick,
                label = { 
                    Text(
                        text = specimen.period.displayName,
                        color = Color.White
                    )
                },
                leadingIcon = {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = getPeriodColor(specimen.period),
                                shape = CircleShape
                            )
                    )
                },
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    containerColor = getPeriodColor(specimen.period),
                    labelColor = Color.White,
                    selectedContainerColor = getPeriodColor(specimen.period),
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.semantics {
                    contentDescription = "Geological period: ${specimen.period.displayName}"
                }
            )
            
            // Tags section (only show if tags exist)
            if (specimen.tagNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    specimen.tagNames.forEach { tag ->
                        AssistChip(
                            onClick = { onTagClick(tag) },
                            label = { 
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Tag: $tag"
                            }
                        )
                    }
                }
            }
            
            // Inventory ID if available
            if (!specimen.inventoryId.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "ID: ${specimen.inventoryId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.semantics {
                        contentDescription = "Inventory ID: ${specimen.inventoryId}"
                    }
                )
            }
        }
    }
}