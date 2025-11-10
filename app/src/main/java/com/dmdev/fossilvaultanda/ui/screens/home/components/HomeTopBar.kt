package com.dmdev.fossilvaultanda.ui.screens.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onOpenDrawer: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = "Fossil Collector",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open navigation drawer"
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