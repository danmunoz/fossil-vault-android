package com.dmdev.fossilvaultanda.ui.screens.support.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.dmdev.fossilvaultanda.data.models.FaqItem
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun FaqItemCard(
    faqItem: FaqItem,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggleExpanded() }
            .semantics {
                contentDescription = if (isExpanded) {
                    "FAQ item expanded: ${faqItem.question}"
                } else {
                    "FAQ item collapsed: ${faqItem.question}"
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = FossilVaultSpacing.xs)
    ) {
        Column(
            modifier = Modifier.padding(FossilVaultSpacing.md)
        ) {
            // Question row with expand/collapse icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faqItem.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Animated answer section
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Text(
                        text = "",
                        modifier = Modifier.padding(top = FossilVaultSpacing.sm)
                    )

                    Text(
                        text = faqItem.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FaqItemCardPreview() {
    FossilVaultTheme {
        Column(
            modifier = Modifier.padding(FossilVaultSpacing.md),
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm)
        ) {
            // Collapsed state
            FaqItemCard(
                faqItem = FaqItem(
                    question = "Can I use the app without creating an account?",
                    answer = "Yes! You can use FossilVault without signing up. Your data is stored locally on your device. However, without an account, you won't be able to sync your collection across devices or back it up to the cloud."
                ),
                isExpanded = false,
                onToggleExpanded = {}
            )

            // Expanded state
            FaqItemCard(
                faqItem = FaqItem(
                    question = "What happens if I delete the app without backing up?",
                    answer = "If you're using a local account and delete the app, all your fossil data will be permanently lost. We recommend creating an account to safely back up and sync your collection."
                ),
                isExpanded = true,
                onToggleExpanded = {}
            )
        }
    }
}