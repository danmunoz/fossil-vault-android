package com.dmdev.fossilvaultanda.ui.screens.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onNavigateToStats: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = "Fossil Collector",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Settings and Profile"
                )
            }
        },
        actions = {
            IconButton(onClick = onNavigateToStats) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Statistics"
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