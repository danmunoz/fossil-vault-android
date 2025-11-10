package com.dmdev.fossilvaultanda.ui.screens.subscription.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.PlanComparisonData
import com.dmdev.fossilvaultanda.ui.theme.AccentBlueLight
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultRadius
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing

/**
 * Card displaying a subscription plan with its limits and features.
 *
 * @param planData The plan data to display
 * @param formatStorageSize Function to format storage bytes to readable string
 * @param onSeePricingClick Callback when "See Pricing" button is clicked
 * @param modifier Optional modifier
 */
@Composable
fun PlanComparisonCard(
    planData: PlanComparisonData,
    formatStorageSize: (Long) -> String,
    onSeePricingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(FossilVaultRadius.card),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (planData.isCurrentPlan) {
            BorderStroke(2.dp, AccentBlueLight)
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FossilVaultSpacing.md)
        ) {
            // Header with plan title and current badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = planData.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (planData.isCurrentPlan) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = AccentBlueLight,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "CURRENT",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(FossilVaultSpacing.md))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(FossilVaultSpacing.md))

            // Limits section
            LimitRow(
                label = "Specimens",
                value = planData.specimenLimit?.toString() ?: "Unlimited"
            )
            Spacer(modifier = Modifier.height(FossilVaultSpacing.sm))
            LimitRow(
                label = "Photos per specimen",
                value = planData.photoLimit.toString()
            )
            Spacer(modifier = Modifier.height(FossilVaultSpacing.sm))
            LimitRow(
                label = "Storage",
                value = formatStorageSize(planData.storageLimit)
            )

            Spacer(modifier = Modifier.height(FossilVaultSpacing.md))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(FossilVaultSpacing.md))

            // Features list
            planData.features.forEach { feature ->
                FeatureRow(
                    name = feature.name,
                    included = feature.included
                )
                Spacer(modifier = Modifier.height(FossilVaultSpacing.sm))
            }

            // See Pricing button (only for non-current, purchasable plans)
            if (!planData.isCurrentPlan && planData.canPurchase) {
                Spacer(modifier = Modifier.height(FossilVaultSpacing.sm))
                Button(
                    onClick = onSeePricingClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(FossilVaultRadius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlueLight,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "See Pricing",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
