package com.dmdev.fossilvaultanda.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.Specimen
import com.dmdev.fossilvaultanda.data.models.enums.FossilElement
import com.dmdev.fossilvaultanda.data.models.enums.Period
import kotlinx.datetime.Clock

@Composable
fun SpecimenGrid(
    specimens: List<Specimen>,
    onSpecimenClick: (Specimen) -> Unit = {},
    onSpecimenAction: (Specimen) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (specimens.isEmpty()) {
        EmptyState(
            message = "No specimens found",
            modifier = modifier
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(specimens) { specimen ->
                SpecimenCard(
                    specimen = specimen,
                    onCardClick = { onSpecimenClick(specimen) },
                    onActionClick = { onSpecimenAction(specimen) }
                )
            }
        }
    }
}

@Composable
fun SpecimenList(
    specimens: List<Specimen>,
    onSpecimenClick: (Specimen) -> Unit = {},
    onSpecimenAction: (Specimen) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (specimens.isEmpty()) {
        EmptyState(
            message = "No specimens found",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(specimens) { specimen ->
                SpecimenListItem(
                    specimen = specimen,
                    onCardClick = { onSpecimenClick(specimen) },
                    onActionClick = { onSpecimenAction(specimen) }
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String,
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
                modifier = Modifier.padding(16.dp)
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Try adjusting your search or filters",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun SpecimenGridPreview() {
    val sampleSpecimens = listOf(
        Specimen(
            id = "1",
            userId = "user1",
            species = "Tyrannosaurus rex",
            period = Period.CRETACEOUS,
            element = FossilElement.SKULL,
            location = "Hell Creek Formation, Montana",
            isFavorite = true,
            creationDate = Clock.System.now()
        ),
        Specimen(
            id = "2",
            userId = "user1",
            species = "Triceratops horridus",
            period = Period.CRETACEOUS,
            element = FossilElement.SKULL,
            location = "Hell Creek Formation",
            isFavorite = false,
            creationDate = Clock.System.now()
        )
    )
    
    SpecimenGrid(specimens = sampleSpecimens)
}

@Preview
@Composable
fun SpecimenListPreview() {
    val sampleSpecimens = listOf(
        Specimen(
            id = "1",
            userId = "user1",
            species = "Tyrannosaurus rex",
            period = Period.CRETACEOUS,
            element = FossilElement.SKULL,
            location = "Hell Creek Formation, Montana",
            isFavorite = true,
            creationDate = Clock.System.now()
        ),
        Specimen(
            id = "2",
            userId = "user1",
            species = "Triceratops horridus",
            period = Period.CRETACEOUS,
            element = FossilElement.SKULL,
            location = "Hell Creek Formation",
            isFavorite = false,
            creationDate = Clock.System.now()
        )
    )
    
    SpecimenList(specimens = sampleSpecimens)
}

@Preview
@Composable
fun EmptyStatePreview() {
    EmptyState(message = "No specimens found")
}