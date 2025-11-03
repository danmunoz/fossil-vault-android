package com.dmdev.fossilvaultanda.ui.screens.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.ui.screens.stats.PeriodDistribution
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.fossilVault.geological.GeologicalPeriod
import kotlin.math.min

/**
 * Card component displaying geological period distribution as a donut chart
 */
@Composable
fun PeriodDistributionChart(
    distribution: List<PeriodDistribution>,
    totalCount: Int,
    mostCommonPeriod: GeologicalPeriod?,
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
                text = "Geological Period Distribution",
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
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No specimens to display",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Donut chart with center text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Custom donut chart
                    DonutChart(
                        distribution = distribution,
                        modifier = Modifier.size(280.dp)
                    )

                    // Center text showing total count
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = totalCount.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(FossilVaultSpacing.md))

                // Legend - show only top periods (to avoid overcrowding)
                val topPeriods = distribution.take(6)
                Column(
                    verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.xs)
                ) {
                    topPeriods.chunked(2).forEach { rowPeriods ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
                        ) {
                            rowPeriods.forEach { dist ->
                                LegendItem(
                                    distribution = dist,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Fill empty space if odd number
                            if (rowPeriods.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Stats summary
                HorizontalDivider(modifier = Modifier.padding(vertical = FossilVaultSpacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        label = "Total Specimens",
                        value = totalCount.toString()
                    )

                    StatItem(
                        label = "Most Common Period",
                        value = mostCommonPeriod?.displayName ?: "N/A"
                    )
                }
            }
        }
    }
}

/**
 * Custom donut chart drawn with Canvas
 */
@Composable
private fun DonutChart(
    distribution: List<PeriodDistribution>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasSize = min(size.width, size.height)
        val radius = canvasSize / 2f
        val strokeWidth = radius * 0.3f // 30% of radius for the donut thickness

        val arcSize = Size(canvasSize, canvasSize)
        val arcTopLeft = Offset.Zero

        var startAngle = -90f // Start from top

        distribution.forEach { dist ->
            val sweepAngle = dist.percentage * 3.6f // Convert percentage to degrees

            drawArc(
                color = dist.period.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Butt
                )
            )

            startAngle += sweepAngle
        }
    }
}

/**
 * Legend item showing period color, name, count, and percentage
 */
@Composable
private fun LegendItem(
    distribution: PeriodDistribution,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Color indicator
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(distribution.period.color)
        )

        Spacer(modifier = Modifier.width(FossilVaultSpacing.xs))

        // Period name
        Text(
            text = distribution.period.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(FossilVaultSpacing.xs))

        // Count and percentage
        Text(
            text = "${distribution.count} (${String.format("%.1f", distribution.percentage)}%)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
