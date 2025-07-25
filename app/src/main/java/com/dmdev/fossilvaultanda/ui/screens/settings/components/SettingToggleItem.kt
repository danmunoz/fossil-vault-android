package com.dmdev.fossilvaultanda.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun SettingToggleItem(
    title: String,
    description: String? = null,
    checked: Boolean,
    enabled: Boolean = true,
    isLocked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = FossilVaultSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.xs)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled && !isLocked) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        if (isLocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Setting locked",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = FossilVaultSpacing.md)
            )
        } else {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                modifier = Modifier.padding(start = FossilVaultSpacing.md)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingToggleItemPreview() {
    FossilVaultTheme {
        Column(
            modifier = Modifier.padding(FossilVaultSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
        ) {
            SettingToggleItem(
                title = "Divide Carboniferous Period",
                description = "Show Mississippian and Pennsylvanian as separate periods",
                checked = true,
                onCheckedChange = {}
            )
            
            SettingToggleItem(
                title = "Locked Setting",
                description = "This setting is locked for anonymous users",
                checked = false,
                isLocked = true,
                onCheckedChange = {}
            )
            
            SettingToggleItem(
                title = "Disabled Setting",
                checked = false,
                enabled = false,
                onCheckedChange = {}
            )
        }
    }
}