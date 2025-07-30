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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmdev.fossilvaultanda.data.models.enums.Period
import com.dmdev.fossilvaultanda.ui.theme.Dimensions
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme
import com.dmdev.fossilvaultanda.ui.theme.getPeriodColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodPickerScreen(
    onNavigateBack: () -> Unit,
    onPeriodSelected: (Period) -> Unit,
    selectedPeriod: Period? = null,
    modifier: Modifier = Modifier,
    viewModel: PeriodPickerViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState(initial = null)
    
    // Get periods based on user settings
    val divideCarboniferous = userProfile?.settings?.divideCarboniferous ?: false
    val periods = Period.getAllCases(divideCarboniferous)
    val state = rememberLazyListState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Geological Period",
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state =state,
        ) {
            item {
                Spacer(modifier = Modifier.height(Dimensions.small))
            }
            
            items(periods) { period ->
                PeriodItem(
                    period = period,
                    isSelected = period == selectedPeriod,
                    onClick = {
                        onPeriodSelected(period)
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(Dimensions.medium))
            }
        }
    }
}

@Composable
private fun PeriodItem(
    period: Period,
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
            // Period color indicator
            Card(
                modifier = Modifier.size(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getPeriodColor(period)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                // Color indicator (empty card with color)
            }
            
            Spacer(modifier = Modifier.padding(horizontal = Dimensions.small))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = period.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                if (period != Period.UNKNOWN) {
                    Text(
                        text = getPeriodDescription(period),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
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

private fun getPeriodDescription(period: Period): String {
    return when (period) {
        Period.PRECAMBRIAN -> "4.6 billion - 541 million years ago"
        Period.CAMBRIAN -> "541 - 485 million years ago"
        Period.ORDOVICIAN -> "485 - 444 million years ago"
        Period.SILURIAN -> "444 - 419 million years ago"
        Period.DEVONIAN -> "419 - 359 million years ago"
        Period.CARBONIFEROUS -> "359 - 299 million years ago"
        Period.MISSISSIPPIAN -> "359 - 323 million years ago"
        Period.PENNSYLVANIAN -> "323 - 299 million years ago"
        Period.PERMIAN -> "299 - 252 million years ago"
        Period.TRIASSIC -> "252 - 201 million years ago"
        Period.JURASSIC -> "201 - 145 million years ago"
        Period.CRETACEOUS -> "145 - 66 million years ago"
        Period.PALEOCENE -> "66 - 23 million years ago"
        Period.NEOGENE -> "23 - 2.6 million years ago"
        Period.QUATERNARY -> "2.6 million years ago - present"
        Period.UNKNOWN -> ""
    }
}

// Simple ViewModel for accessing user profile
@dagger.hilt.android.lifecycle.HiltViewModel
class PeriodPickerViewModel @javax.inject.Inject constructor(
    private val databaseManager: com.dmdev.fossilvaultanda.data.repository.interfaces.DatabaseManaging
) : androidx.lifecycle.ViewModel() {
    
    val userProfile = databaseManager.profile
}

@Preview(showBackground = true)
@Composable
fun PeriodPickerScreenPreview() {
    FossilVaultTheme {
        PeriodPickerScreen(
            onNavigateBack = { },
            onPeriodSelected = { },
            selectedPeriod = Period.CRETACEOUS
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PeriodItemPreview() {
    FossilVaultTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PeriodItem(
                period = Period.CRETACEOUS,
                isSelected = true,
                onClick = { }
            )
            
            PeriodItem(
                period = Period.JURASSIC,
                isSelected = false,
                onClick = { }
            )
            
            PeriodItem(
                period = Period.MISSISSIPPIAN,
                isSelected = false,
                onClick = { }
            )
        }
    }
}