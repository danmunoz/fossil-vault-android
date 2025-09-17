package com.dmdev.fossilvaultanda.ui.screens.specimen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmdev.fossilvaultanda.ui.theme.Dimensions
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme
import com.fossilVault.geological.GeologicalTime
import com.fossilVault.geological.GeologicalEra
import com.fossilVault.geological.GeologicalPeriod
import com.fossilVault.geological.GeologicalEpoch
import com.fossilVault.geological.GeologicalAge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedGeologicalTimePickerScreen(
    onNavigateBack: () -> Unit,
    onGeologicalTimeSelected: (GeologicalTime) -> Unit,
    initialGeologicalTime: GeologicalTime? = null,
    modifier: Modifier = Modifier,
    viewModel: AdvancedGeologicalTimePickerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Initialize with provided geological time
    LaunchedEffect(initialGeologicalTime) {
        viewModel.initializeWithGeologicalTime(initialGeologicalTime)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Geological Time",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = { viewModel.clearSelection() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Clear")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimensions.medium),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Current Selection Summary
                item {
                    CurrentSelectionSummary(
                        geologicalTime = uiState.currentGeologicalTime,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Era Section
                item {
                    GeologicalLevelSection(
                        title = "Geological Era",
                        level = GeologicalLevel.ERA,
                        isExpanded = GeologicalLevel.ERA in uiState.expandedSections,
                        onToggleExpanded = { viewModel.toggleSection(GeologicalLevel.ERA) },
                        selectedItem = uiState.selectedEra,
                        onItemSelected = { viewModel.selectEra(it) }
                    ) {
                        GeologicalEra.values().forEach { era ->
                            GeologicalEraItem(
                                era = era,
                                isSelected = era == uiState.selectedEra,
                                onClick = { viewModel.selectEra(era) }
                            )
                        }
                    }
                }

                // Period Section
                item {
                    GeologicalLevelSection(
                        title = "Geological Period",
                        level = GeologicalLevel.PERIOD,
                        isExpanded = GeologicalLevel.PERIOD in uiState.expandedSections,
                        onToggleExpanded = { viewModel.toggleSection(GeologicalLevel.PERIOD) },
                        selectedItem = uiState.selectedPeriod,
                        onItemSelected = { viewModel.selectPeriod(it) }
                    ) {
                        uiState.availablePeriods.forEach { period ->
                            GeologicalPeriodItem(
                                period = period,
                                isSelected = period == uiState.selectedPeriod,
                                onClick = { viewModel.selectPeriod(period) }
                            )
                        }
                    }
                }

                // Epoch Section
                if (uiState.availableEpochs.isNotEmpty()) {
                    item {
                        GeologicalLevelSection(
                            title = "Geological Epoch",
                            level = GeologicalLevel.EPOCH,
                            isExpanded = GeologicalLevel.EPOCH in uiState.expandedSections,
                            onToggleExpanded = { viewModel.toggleSection(GeologicalLevel.EPOCH) },
                            selectedItem = uiState.selectedEpoch,
                            onItemSelected = { viewModel.selectEpoch(it) }
                        ) {
                            uiState.availableEpochs.forEach { epoch ->
                                GeologicalEpochItem(
                                    epoch = epoch,
                                    isSelected = epoch == uiState.selectedEpoch,
                                    onClick = { viewModel.selectEpoch(epoch) }
                                )
                            }
                        }
                    }
                }

                // Age Section
                if (uiState.availableAges.isNotEmpty()) {
                    item {
                        GeologicalLevelSection(
                            title = "Geological Age",
                            level = GeologicalLevel.AGE,
                            isExpanded = GeologicalLevel.AGE in uiState.expandedSections,
                            onToggleExpanded = { viewModel.toggleSection(GeologicalLevel.AGE) },
                            selectedItem = uiState.selectedAge,
                            onItemSelected = { viewModel.selectAge(it) }
                        ) {
                            uiState.availableAges.forEach { age ->
                                GeologicalAgeItem(
                                    age = age,
                                    isSelected = age == uiState.selectedAge,
                                    onClick = { viewModel.selectAge(age) }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Bottom action buttons
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimensions.medium),
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.medium)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onGeologicalTimeSelected(uiState.currentGeologicalTime)
                        },
                        enabled = uiState.hasValidSelection,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Select")
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrentSelectionSummary(
    geologicalTime: GeologicalTime,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimensions.medium)
        ) {
            Text(
                text = "Current Selection",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            val hierarchyText = geologicalTime.displayString
            Text(
                text = if (hierarchyText.isNotBlank()) hierarchyText else "No selection",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            geologicalTime.timeRange?.let { timeRange ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeRange,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun <T> GeologicalLevelSection(
    title: String,
    level: GeologicalLevel,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    selectedItem: T?,
    onItemSelected: (T?) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpanded() }
                    .padding(Dimensions.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    selectedItem?.let { item ->
                        Text(
                            text = when (item) {
                                is GeologicalEra -> item.displayName
                                is GeologicalPeriod -> item.displayName
                                is GeologicalEpoch -> item.displayName
                                is GeologicalAge -> item.displayName
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expandable Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    content()
                }
            }
        }
    }
}

@Composable
private fun GeologicalEraItem(
    era: GeologicalEra,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GeologicalTimeItem(
        displayName = era.displayName,
        timeRange = era.timeRange,
        color = Color.Gray, // Era doesn't have specific colors
        isSelected = isSelected,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun GeologicalPeriodItem(
    period: GeologicalPeriod,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GeologicalTimeItem(
        displayName = period.displayName,
        timeRange = period.timeRange,
        color = period.color,
        isSelected = isSelected,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun GeologicalEpochItem(
    epoch: GeologicalEpoch,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GeologicalTimeItem(
        displayName = epoch.displayName,
        timeRange = epoch.timeRange,
        color = epoch.period.color, // Use parent period color
        isSelected = isSelected,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun GeologicalAgeItem(
    age: GeologicalAge,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GeologicalTimeItem(
        displayName = age.displayName,
        timeRange = age.timeRange,
        color = age.getEpoch().period.color, // Use grandparent period color
        isSelected = isSelected,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun GeologicalTimeItem(
    displayName: String,
    timeRange: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        }
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = Dimensions.medium, vertical = 4.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Text(
                    text = timeRange,
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
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdvancedGeologicalTimePickerScreenPreview() {
    FossilVaultTheme {
        AdvancedGeologicalTimePickerScreen(
            onNavigateBack = { },
            onGeologicalTimeSelected = { }
        )
    }
}