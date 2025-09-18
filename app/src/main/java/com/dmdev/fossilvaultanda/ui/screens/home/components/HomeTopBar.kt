package com.dmdev.fossilvaultanda.ui.screens.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onNavigateToStats: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = "Fossil Collector",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = {
                        expanded = false
                        onNavigateToSettings()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Statistics") },
                    onClick = {
                        expanded = false
                        onNavigateToStats()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null
                        )
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Preview
@Composable
fun HomeTopBarPreview() {
    HomeTopBar()
}