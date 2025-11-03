package com.dmdev.fossilvaultanda.ui.screens.stats.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.fossilVault.geological.GeologicalPeriod
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing

/**
 * Bottom sheet for selecting geological periods (multi-select)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodFilterBottomSheet(
    currentSelection: Set<GeologicalPeriod>,
    onDismiss: () -> Unit,
    onApply: (Set<GeologicalPeriod>) -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier
) {
    var selectedPeriods by remember(currentSelection) {
        mutableStateOf(currentSelection.toMutableSet())
    }

    val allPeriods = GeologicalPeriod.getAllCases(divideCarboniferous = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = FossilVaultSpacing.lg)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = FossilVaultSpacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Geological Periods",
                    style = MaterialTheme.typography.headlineSmall
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.xs)
                ) {
                    TextButton(
                        onClick = { selectedPeriods.clear() }
                    ) {
                        Text("Clear All")
                    }

                    TextButton(
                        onClick = { selectedPeriods.addAll(allPeriods) }
                    ) {
                        Text("Select All")
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = FossilVaultSpacing.sm))

            // Period list
            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
            ) {
                items(allPeriods) { period ->
                    PeriodCheckboxItem(
                        period = period,
                        isChecked = period in selectedPeriods,
                        onCheckedChange = { checked ->
                            if (checked) {
                                selectedPeriods.add(period)
                            } else {
                                selectedPeriods.remove(period)
                            }
                        }
                    )
                }
            }

            // Apply button
            Button(
                onClick = {
                    onApply(selectedPeriods.toSet())
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = FossilVaultSpacing.lg)
                    .padding(top = FossilVaultSpacing.md)
            ) {
                val count = selectedPeriods.size
                val text = if (count == 0) "Show All Periods" else "Apply ($count selected)"
                Text(text)
            }
        }
    }
}

/**
 * Individual period checkbox item with color indicator
 */
@Composable
private fun PeriodCheckboxItem(
    period: GeologicalPeriod,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = isChecked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox
            )
            .padding(
                horizontal = FossilVaultSpacing.lg,
                vertical = FossilVaultSpacing.sm
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = null
        )

        // Color indicator
        Box(
            modifier = Modifier
                .padding(start = FossilVaultSpacing.md, end = FossilVaultSpacing.sm)
                .size(16.dp)
                .clip(CircleShape)
                .background(period.color)
        )

        Text(
            text = period.displayName,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
