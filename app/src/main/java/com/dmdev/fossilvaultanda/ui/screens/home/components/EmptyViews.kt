package com.dmdev.fossilvaultanda.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun NoSpecimensEmpty(
    onAddSpecimen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Start your fossil collection",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Add your first specimen to get started",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = onAddSpecimen
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Add Specimen")
            }
        }
    }
}

@Composable
fun NoResultsEmpty(
    hasFiltersActive: Boolean = false,
    onClearFilters: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "No specimens found",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Try adjusting your search or filters above",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = if (hasFiltersActive) 24.dp else 0.dp)
            )

            if (hasFiltersActive) {
                OutlinedButton(
                    onClick = onClearFilters
                ) {
                    Text("Clear Filters")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoSpecimensEmptyPreview() {
    FossilVaultTheme {
        NoSpecimensEmpty()
    }
}

@Preview(showBackground = true)
@Composable
fun NoResultsEmptyWithFiltersPreview() {
    FossilVaultTheme {
        NoResultsEmpty(hasFiltersActive = true)
    }
}

@Preview(showBackground = true)
@Composable
fun NoResultsEmptyWithoutFiltersPreview() {
    FossilVaultTheme {
        NoResultsEmpty(hasFiltersActive = false)
    }
}