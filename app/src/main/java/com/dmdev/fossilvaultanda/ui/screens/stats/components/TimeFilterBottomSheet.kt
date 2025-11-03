package com.dmdev.fossilvaultanda.ui.screens.stats.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.ui.screens.stats.TimeFilter
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing

/**
 * Bottom sheet for selecting time filter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFilterBottomSheet(
    currentFilter: TimeFilter,
    onDismiss: () -> Unit,
    onFilterSelected: (TimeFilter) -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember(currentFilter) { mutableStateOf(currentFilter) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = FossilVaultSpacing.xl)
        ) {
            // Header
            Text(
                text = "Time Period",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(
                    horizontal = FossilVaultSpacing.lg,
                    vertical = FossilVaultSpacing.md
                )
            )

            HorizontalDivider()

            // Radio button group
            Column(
                modifier = Modifier
                    .selectableGroup()
                    .padding(vertical = FossilVaultSpacing.sm)
            ) {
                TimeFilter.entries.forEach { filter ->
                    TimeFilterOption(
                        filter = filter,
                        isSelected = selectedFilter == filter,
                        onClick = {
                            selectedFilter = filter
                            onFilterSelected(filter)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Individual time filter radio option
 */
@Composable
private fun TimeFilterOption(
    filter: TimeFilter,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(
                horizontal = FossilVaultSpacing.lg,
                vertical = FossilVaultSpacing.md
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )

        Text(
            text = filter.displayName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = FossilVaultSpacing.md)
        )
    }
}
