package com.dmdev.fossilvaultanda.ui.screens.specimen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.ui.theme.Dimensions
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(Dimensions.medium))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isRequired: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(label)
                    if (isRequired) {
                        Text(
                            text = " *",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder) }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            isError = errorMessage != null,
            trailingIcon = if (errorMessage != null) {
                {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            } else null
        )
        
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage as String,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = Dimensions.medium)
            )
        }
    }
}

@Composable
fun ValidatedNumericField(
    value: Double?,
    onValueChange: (Double?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    ValidatedTextField(
        value = value?.toString() ?: "",
        onValueChange = { input ->
            val parsed = input.toDoubleOrNull()
            onValueChange(parsed)
        },
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        errorMessage = errorMessage,
        keyboardType = KeyboardType.Decimal,
        imeAction = imeAction,
        keyboardActions = keyboardActions
    )
}

@Composable
fun SelectionField(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select...",
    isRequired: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .clickable { onClick() }
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(label)
                        if (isRequired) {
                            Text(
                                text = " *",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                placeholder = { Text(placeholder) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                isError = errorMessage != null,
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (errorMessage != null) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Select",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (errorMessage != null) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = if (errorMessage != null)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage as String,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = Dimensions.medium)
            )
        }
    }
}

@Composable
fun DimensionInputRow(
    width: Double?,
    height: Double?,
    length: Double?,
    unit: String,
    onWidthChange: (Double?) -> Unit,
    onHeightChange: (Double?) -> Unit,
    onLengthChange: (Double?) -> Unit,
    modifier: Modifier = Modifier,
    widthError: String? = null,
    heightError: String? = null,
    lengthError: String? = null
) {
    Column(modifier = modifier) {
        Text(
            text = "Dimensions ($unit)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ValidatedNumericField(
                value = width,
                onValueChange = onWidthChange,
                label = "Width",
                modifier = Modifier.weight(1f),
                errorMessage = widthError
            )
            
            ValidatedNumericField(
                value = height,
                onValueChange = onHeightChange,
                label = "Height",
                modifier = Modifier.weight(1f),
                errorMessage = heightError
            )
            
            ValidatedNumericField(
                value = length,
                onValueChange = onLengthChange,
                label = "Length",
                modifier = Modifier.weight(1f),
                errorMessage = lengthError
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormComponentsPreview() {
    FossilVaultTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionHeader(
                title = "Basic Information",
                icon = Icons.Default.ChevronRight, // Would use proper icon
                iconColor = MaterialTheme.colorScheme.primary
            )
            
            ValidatedTextField(
                value = "Tyrannosaurus Rex",
                onValueChange = { },
                label = "Species",
                isRequired = true
            )
            
            ValidatedTextField(
                value = "",
                onValueChange = { },
                label = "Species",
                isRequired = true,
                errorMessage = "Species name is required"
            )
            
            SelectionField(
                value = "Cretaceous",
                label = "Geological Period",
                onClick = { },
                isRequired = true
            )
            
            DimensionInputRow(
                width = 10.5,
                height = 5.2,
                length = 8.1,
                unit = "cm",
                onWidthChange = { },
                onHeightChange = { },
                onLengthChange = { }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: kotlinx.datetime.Instant?,
    onDateSelected: (kotlinx.datetime.Instant?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "Select date",
    isRequired: Boolean = false,
    errorMessage: String? = null
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Format date for display
    val displayText = selectedDate?.let { instant ->
        val localDate = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        "${localDate.monthNumber.toString().padStart(2, '0')}/${localDate.dayOfMonth.toString().padStart(2, '0')}/${localDate.year}"
    }
    
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .clickable { showDatePicker = true }
        ) {
            OutlinedTextField(
                value = displayText ?: "",
                onValueChange = { },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(label)
                        if (isRequired) {
                            Text(
                                text = " *",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                placeholder = { Text(placeholder) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                isError = errorMessage != null,
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (errorMessage != null) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Select date",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = if (errorMessage != null) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = if (errorMessage != null)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage as String,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = Dimensions.medium)
            )
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.toEpochMilliseconds()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                            onDateSelected(instant)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}