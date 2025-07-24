package com.dmdev.fossilvaultanda.ui.screens.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.enums.Period

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedPeriod: Period?,
    onPeriodChange: (Period?) -> Unit,
    resultCount: Int,
    modifier: Modifier = Modifier
) {
    var active by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.padding(16.dp)) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            placeholder = {
                Text("Search your collection")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Search suggestions could go here in the future
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Period filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Period:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            // All periods chip
            FilterChip(
                onClick = { onPeriodChange(null) },
                label = { Text("All") },
                selected = selectedPeriod == null
            )
            
            // Individual period chips
            Period.getAllCases(divideCarboniferous = false).forEach { period ->
                if (period != Period.UNKNOWN) {
                    FilterChip(
                        onClick = { 
                            onPeriodChange(if (selectedPeriod == period) null else period)
                        },
                        label = { Text(period.displayName) },
                        selected = selectedPeriod == period
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Result count
        Text(
            text = when (resultCount) {
                0 -> "No specimens found"
                1 -> "1 specimen"
                else -> "$resultCount specimens"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Preview
@Composable
fun SearchAndFilterSectionPreview() {
    SearchAndFilterSection(
        searchQuery = "",
        onSearchQueryChange = {},
        selectedPeriod = null,
        onPeriodChange = {},
        resultCount = 42
    )
}