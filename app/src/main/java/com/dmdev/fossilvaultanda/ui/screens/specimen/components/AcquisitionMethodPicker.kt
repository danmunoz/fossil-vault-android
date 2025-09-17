package com.dmdev.fossilvaultanda.ui.screens.specimen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import com.dmdev.fossilvaultanda.data.models.enums.AcquisitionMethod
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcquisitionMethodPicker(
    selectedMethod: AcquisitionMethod?,
    onMethodChange: (AcquisitionMethod?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Acquisition Method"
) {
    var expanded by remember { mutableStateOf(false) }
    val methods = listOf(null) + AcquisitionMethod.getAllCases()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedMethod?.displayString ?: "Not specified",
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
            methods.forEach { method ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = method?.displayString ?: "Not specified",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onMethodChange(method)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AcquisitionMethodPickerPreview() {
    FossilVaultTheme {
        AcquisitionMethodPicker(
            selectedMethod = AcquisitionMethod.FOUND,
            onMethodChange = { }
        )
    }
}