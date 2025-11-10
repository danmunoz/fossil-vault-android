package com.dmdev.fossilvaultanda.ui.screens.stats.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.ui.screens.stats.CountryDistribution
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.util.CountryUtils

/**
 * Card component displaying country distribution as a horizontal bar chart
 */
@Composable
fun CountryDistributionChart(
    distribution: List<CountryDistribution>,
    totalCount: Int,
    topCountry: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FossilVaultSpacing.lg)
        ) {
            // Title
            Text(
                text = "Specimens by Country",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(FossilVaultSpacing.lg))

            // Chart or empty state
            if (distribution.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No specimens with country data",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Horizontal bar chart
                Column(
                    verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
                ) {
                    distribution.forEach { dist ->
                        CountryBarItem(
                            distribution = dist,
                            maxCount = distribution.first().count
                        )
                    }
                }

                // Stats summary
                HorizontalDivider(modifier = Modifier.padding(vertical = FossilVaultSpacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        label = "Total Countries",
                        value = distribution.size.toString()
                    )

                    StatItem(
                        label = "Top Country",
                        value = topCountry?.let { CountryUtils.getLocalizedCountryName(it) } ?: "N/A"
                    )
                }
            }
        }
    }
}

/**
 * Horizontal bar item showing country name, bar, and count
 */
@Composable
private fun CountryBarItem(
    distribution: CountryDistribution,
    maxCount: Int,
    modifier: Modifier = Modifier
) {
    // Calculate bar width as fraction of max count
    val fraction = distribution.count.toFloat() / maxCount.toFloat()

    // Animated width for smooth appearance
    val animatedFraction = remember { Animatable(0f) }

    LaunchedEffect(fraction) {
        animatedFraction.animateTo(
            targetValue = fraction,
            animationSpec = tween(durationMillis = 600)
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.xs)
    ) {
        // Country name
        Text(
            text = CountryUtils.getLocalizedCountryName(distribution.country),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Bar and count
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm)
        ) {
            // Horizontal bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction.value)
                        .height(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF66BB6A)) // Material green 400
                )
            }

            // Count
            Text(
                text = distribution.count.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = FossilVaultSpacing.xs)
            )
        }
    }
}

/**
 * Stat item showing a label and value
 */
@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
