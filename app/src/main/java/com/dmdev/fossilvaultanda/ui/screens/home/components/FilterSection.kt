package com.dmdev.fossilvaultanda.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.enums.DisplayMode
import com.dmdev.fossilvaultanda.data.models.enums.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit,
    displayMode: DisplayMode,
    onDisplayModeChange: (DisplayMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sort dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                value = sortOption.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sort by") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                SortOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            onSortChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
        
        // Display mode toggle buttons
        Row {
            IconToggleButton(
                checked = displayMode == DisplayMode.GRID,
                onCheckedChange = { if (it) onDisplayModeChange(DisplayMode.GRID) }
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Grid view",
                    tint = if (displayMode == DisplayMode.GRID) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            IconToggleButton(
                checked = displayMode == DisplayMode.LIST,
                onCheckedChange = { if (it) onDisplayModeChange(DisplayMode.LIST) }
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "List view",
                    tint = if (displayMode == DisplayMode.LIST) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun FilterSectionPreview() {
    FilterSection(
        sortOption = SortOption.RECENT,
        onSortChange = {},
        displayMode = DisplayMode.GRID,
        onDisplayModeChange = {}
    )
}