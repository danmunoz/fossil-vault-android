package com.dmdev.fossilvaultanda.ui.screens.specimen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.data.models.enums.FossilElement
import com.dmdev.fossilvaultanda.ui.theme.Dimensions
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElementPickerScreen(
    onNavigateBack: () -> Unit,
    onElementSelected: (FossilElement, String) -> Unit, // element and custom text
    selectedElement: FossilElement? = null,
    customElementText: String = "",
    modifier: Modifier = Modifier
) {
    var customText by remember { mutableStateOf(customElementText) }
    val focusManager = LocalFocusManager.current
    
    val elements = FossilElement.values().toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Fossil Element",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimensions.medium),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(Dimensions.small))
            }
            
            items(elements) { element ->
                ElementItem(
                    element = element,
                    isSelected = element == selectedElement,
                    onClick = {
                        if (element == FossilElement.OTHER) {
                            // Don't navigate back immediately for "Other" - just select it
                            // User will enter text and click Done button
                        } else {
                            onElementSelected(element, "")
                        }
                    }
                )

                // Show custom text field when "Other" is selected
                if (element == FossilElement.OTHER && element == selectedElement) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customText,
                        onValueChange = {
                            customText = it
                            // Don't call onElementSelected here - just update local state
                        },
                        label = { Text("Specify element type") },
                        placeholder = { Text("e.g., Scale, Gastralia, etc.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimensions.medium),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (customText.isNotBlank()) {
                                    onElementSelected(element, customText)
                                }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (customText.isNotBlank()) {
                                onElementSelected(element, customText)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimensions.medium),
                        enabled = customText.isNotBlank()
                    ) {
                        Text("Done")
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(Dimensions.medium))
            }
        }
    }
}

@Composable
private fun ElementItem(
    element: FossilElement,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Element icon placeholder (could add actual icons later)
            Card(
                modifier = Modifier.size(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getElementColor(element)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                // Icon placeholder - could add actual fossil icons
            }
            
            Spacer(modifier = Modifier.width(Dimensions.medium))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = element.displayString,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                Text(
                    text = getElementDescription(element),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun getElementColor(element: FossilElement): androidx.compose.ui.graphics.Color {
    return when (element) {
        FossilElement.TOOTH -> androidx.compose.ui.graphics.Color(0xFFFFEB3B) // Yellow
        FossilElement.JAW -> androidx.compose.ui.graphics.Color(0xFFFF9800) // Orange
        FossilElement.SKULL -> androidx.compose.ui.graphics.Color(0xFFF44336) // Red
        FossilElement.SKELETON -> androidx.compose.ui.graphics.Color(0xFFE1E1E1) // Pale Gray
        FossilElement.BONE -> androidx.compose.ui.graphics.Color(0xFFE0E0E0) // Light Gray
        FossilElement.CLAW -> androidx.compose.ui.graphics.Color(0xFF795548) // Brown
        FossilElement.SHELL -> androidx.compose.ui.graphics.Color(0xFF00BCD4) // Cyan
        FossilElement.TRILOBITE -> androidx.compose.ui.graphics.Color(0xFF3F51B5) // Indigo
        FossilElement.MATRIX -> androidx.compose.ui.graphics.Color(0xFF8BC34A) // Light Green
        FossilElement.IMPRINT -> androidx.compose.ui.graphics.Color(0xFF9E9E9E) // Gray
        FossilElement.EGG -> androidx.compose.ui.graphics.Color(0xFFFFE0B2) // Light Orange
        FossilElement.URCHIN -> androidx.compose.ui.graphics.Color(0xFF9C27B0) // Purple
        FossilElement.ICHNOFOSSIL -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // Green
        FossilElement.OTHER -> androidx.compose.ui.graphics.Color(0xFF757575) // Medium Gray
    }
}

private fun getElementDescription(element: FossilElement): String {
    return when (element) {
        FossilElement.TOOTH -> "Fossilized tooth or dental element"
        FossilElement.JAW -> "Jaw bone or mandible"
        FossilElement.SKULL -> "Cranial bones or skull fragments"
        FossilElement.SKELETON -> "Complete or partial skeleton"
        FossilElement.BONE -> "General skeletal bone element"
        FossilElement.CLAW -> "Claw or ungual phalanx"
        FossilElement.SHELL -> "Shell or carapace fragment"
        FossilElement.TRILOBITE -> "Trilobite or arthropod fossil"
        FossilElement.MATRIX -> "Rock matrix with embedded fossils"
        FossilElement.IMPRINT -> "Impression or mold fossil"
        FossilElement.EGG -> "Fossilized egg or eggshell"
        FossilElement.URCHIN -> "Sea urchin or echinoid fossil"
        FossilElement.ICHNOFOSSIL -> "Trace fossil or trackway"
        FossilElement.OTHER -> "Other fossil element type"
    }
}

@Preview(showBackground = true)
@Composable
fun ElementPickerScreenPreview() {
    FossilVaultTheme {
        ElementPickerScreen(
            onNavigateBack = { },
            onElementSelected = { _, _ -> },
            selectedElement = FossilElement.TOOTH
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ElementItemPreview() {
    FossilVaultTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ElementItem(
                element = FossilElement.TOOTH,
                isSelected = true,
                onClick = { }
            )
            
            ElementItem(
                element = FossilElement.BONE,
                isSelected = false,
                onClick = { }
            )
            
            ElementItem(
                element = FossilElement.OTHER,
                isSelected = false,
                onClick = { }
            )
        }
    }
}