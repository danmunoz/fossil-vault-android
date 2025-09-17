package com.dmdev.fossilvaultanda.ui.screens.specimen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.enums.Condition
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionPicker(
    selectedCondition: Condition?,
    onConditionChange: (Condition?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Condition"
) {
    var expanded by remember { mutableStateOf(false) }
    var customCondition by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

    val conditions = listOf(null) + Condition.getAllCasesForUI()

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = when {
                    selectedCondition == null -> "Not specified"
                    Condition.isOtherPlaceholder(selectedCondition) -> "Other"
                    selectedCondition is Condition.Other -> selectedCondition.customValue
                    else -> selectedCondition.displayString
                },
                onValueChange = { },
                readOnly = true,
                label = { Text(label) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                conditions.forEach { condition ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = when {
                                    condition == null -> "Not specified"
                                    Condition.isOtherPlaceholder(condition) -> "Other"
                                    else -> condition.displayString
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            if (condition != null && Condition.isOtherPlaceholder(condition)) {
                                showCustomInput = true
                                expanded = false
                            } else {
                                onConditionChange(condition)
                                showCustomInput = false
                                expanded = false
                            }
                        }
                    )
                }
            }
        }

        // Custom condition input
        if (showCustomInput) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = customCondition,
                onValueChange = { customCondition = it },
                label = { Text("Custom Condition") },
                placeholder = { Text("Enter custom condition") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            if (customCondition.isNotBlank()) {
                                onConditionChange(Condition.Other(customCondition))
                                showCustomInput = false
                                customCondition = ""
                            }
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConditionPickerPreview() {
    FossilVaultTheme {
        Column {
            ConditionPicker(
                selectedCondition = Condition.Natural,
                onConditionChange = { }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ConditionPicker(
                selectedCondition = Condition.Other("Custom restoration"),
                onConditionChange = { }
            )
        }
    }
}