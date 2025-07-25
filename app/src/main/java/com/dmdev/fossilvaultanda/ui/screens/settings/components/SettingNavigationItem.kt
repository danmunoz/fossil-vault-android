package com.dmdev.fossilvaultanda.ui.screens.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun SettingNavigationItem(
    title: String,
    subtitle: String? = null,
    value: String? = null,
    enabled: Boolean = true,
    isLocked: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled && !isLocked) { onClick() }
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
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm)
        ) {
            if (value != null && !isLocked) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Setting locked",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Navigate",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingNavigationItemPreview() {
    FossilVaultTheme {
        Column(
            modifier = Modifier.padding(FossilVaultSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
        ) {
            SettingNavigationItem(
                title = "Measurement Unit",
                subtitle = "Default unit for specimen dimensions",
                value = "Millimeters",
                onClick = {}
            )
            
            SettingNavigationItem(
                title = "Default Currency",
                subtitle = "Currency for monetary values",
                value = "$ US Dollar",
                onClick = {}
            )
            
            SettingNavigationItem(
                title = "Locked Setting",
                subtitle = "This setting is locked for anonymous users",
                isLocked = true,
                onClick = {}
            )
            
            SettingNavigationItem(
                title = "Disabled Setting",
                enabled = false,
                onClick = {}
            )
        }
    }
}