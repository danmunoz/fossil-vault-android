package com.dmdev.fossilvaultanda.ui.screens.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.semantics.Role
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing

/**
 * Bottom sheet for selecting countries/regions (multi-select)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryFilterBottomSheet(
    availableCountries: List<String>,
    currentSelection: Set<String>,
    onDismiss: () -> Unit,
    onApply: (Set<String>) -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier
) {
    var selectedCountries by remember(currentSelection) {
        mutableStateOf(currentSelection.toMutableSet())
    }

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
                    text = "Regions",
                    style = MaterialTheme.typography.headlineSmall
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.xs)
                ) {
                    TextButton(
                        onClick = { selectedCountries.clear() }
                    ) {
                        Text("Clear All")
                    }

                    TextButton(
                        onClick = { selectedCountries.addAll(availableCountries) }
                    ) {
                        Text("Select All")
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = FossilVaultSpacing.sm))

            // Country list or empty state
            if (availableCountries.isEmpty()) {
                Text(
                    text = "No regions available in your collection",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(FossilVaultSpacing.lg)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth()
                ) {
                    items(availableCountries) { country ->
                        CountryCheckboxItem(
                            country = country,
                            isChecked = country in selectedCountries,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedCountries.add(country)
                                } else {
                                    selectedCountries.remove(country)
                                }
                            }
                        )
                    }
                }
            }

            // Apply button
            Button(
                onClick = {
                    onApply(selectedCountries.toSet())
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = FossilVaultSpacing.lg)
                    .padding(top = FossilVaultSpacing.md),
                enabled = availableCountries.isNotEmpty()
            ) {
                val count = selectedCountries.size
                val text = if (count == 0) "Show All Regions" else "Apply ($count selected)"
                Text(text)
            }
        }
    }
}

/**
 * Individual country checkbox item
 */
@Composable
private fun CountryCheckboxItem(
    country: String,
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

        Text(
            text = country,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = FossilVaultSpacing.md)
        )
    }
}
