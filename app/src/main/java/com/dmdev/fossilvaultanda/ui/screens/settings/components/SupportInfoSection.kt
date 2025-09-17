package com.dmdev.fossilvaultanda.ui.screens.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme
import com.dmdev.fossilvaultanda.util.EmailUtil

@Composable
fun SupportInfoSection(
    onNavigateToFAQ: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm)
    ) {
        // Section Header
        Text(
            text = "Support & Info",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = FossilVaultSpacing.xs)
        )

        // FAQ
        SettingNavigationItem(
            title = "FAQ",
            subtitle = "Frequently asked questions",
            onClick = onNavigateToFAQ
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(vertical = FossilVaultSpacing.xs)
        )

        // Send Feedback
        SettingNavigationItem(
            title = "Send Feedback",
            subtitle = "Report bugs or suggest improvements",
            onClick = { EmailUtil.sendFeedback(context) }
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(vertical = FossilVaultSpacing.xs)
        )

        // About
        SettingNavigationItem(
            title = "About",
            subtitle = "App information and legal",
            onClick = onNavigateToAbout
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SupportInfoSectionPreview() {
    FossilVaultTheme {
        Column(
            modifier = Modifier.padding(FossilVaultSpacing.md)
        ) {
            SupportInfoSection(
                onNavigateToFAQ = {},
                onNavigateToAbout = {}
            )
        }
    }
}