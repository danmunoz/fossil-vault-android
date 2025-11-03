package com.dmdev.fossilvaultanda.ui.screens.csvimport

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmdev.fossilvaultanda.data.csv.models.*
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultRadius
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing

/**
 * Main CSV Import Screen that handles all import states
 */
@Composable
fun ImportScreen(
    onNavigateBack: () -> Unit,
    onImportComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Show error snackbar if needed
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            viewModel.clearError()
        }
    }

    when (val state = uiState) {
        is ImportUiState.FileSelection -> {
            FileSelectionContent(
                onFileSelected = { uri, fileName ->
                    viewModel.parseCSVFile(uri, fileName)
                },
                onNavigateBack = onNavigateBack
            )
        }
        is ImportUiState.Loading -> {
            LoadingContent(message = state.message, onNavigateBack = onNavigateBack)
        }
        is ImportUiState.FieldMapping -> {
            FieldMappingContent(
                viewModel = viewModel,
                onNavigateBack = { viewModel.goBack() },
                onContinue = { viewModel.proceedToPreview() }
            )
        }
        is ImportUiState.Preview -> {
            PreviewContent(
                viewModel = viewModel,
                onNavigateBack = { viewModel.goBack() },
                onImport = { viewModel.executeImport() }
            )
        }
        is ImportUiState.Importing -> {
            ImportingContent(
                viewModel = viewModel,
                onNavigateBack = onNavigateBack
            )
        }
        is ImportUiState.Summary -> {
            SummaryContent(
                viewModel = viewModel,
                onDone = onImportComplete,
                onStartOver = { viewModel.resetImport() }
            )
        }
        is ImportUiState.Error -> {
            ErrorContent(
                message = state.message,
                onRetry = { viewModel.resetImport() },
                onNavigateBack = onNavigateBack
            )
        }
    }
}

/**
 * File Selection Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileSelectionContent(
    onFileSelected: (Uri, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = uri.lastPathSegment ?: "import.csv"
            onFileSelected(it, fileName)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import from CSV") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(FossilVaultSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.UploadFile,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(FossilVaultSpacing.lg))
            Text(
                "Select CSV File",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(FossilVaultSpacing.sm))
            Text(
                "Choose a CSV file to import your fossil specimens",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(FossilVaultSpacing.xl))
            Button(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.FolderOpen, contentDescription = null)
                Spacer(modifier = Modifier.width(FossilVaultSpacing.sm))
                Text("Choose File")
            }
        }
    }
}

/**
 * Loading Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingContent(
    message: String,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Importing") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(FossilVaultSpacing.md))
                Text(message)
            }
        }
    }
}

/**
 * Field Mapping Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldMappingContent(
    viewModel: ImportViewModel,
    onNavigateBack: () -> Unit,
    onContinue: () -> Unit
) {
    val config by viewModel.mappingConfiguration.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map Fields") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(FossilVaultSpacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    config?.let { cfg ->
                        Text(
                            "${cfg.getMappedFields().size} of ${SpecimenField.entries.size} fields mapped",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Button(
                        onClick = onContinue,
                        enabled = config?.hasAllRequiredFieldsMapped() == true
                    ) {
                        Text("Continue")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = FossilVaultSpacing.md)
        ) {
            config?.let { cfg ->
                FieldCategory.entries.forEach { category ->
                    val fieldsInCategory = SpecimenField.fieldsInCategory(category)
                    if (fieldsInCategory.isNotEmpty()) {
                        item {
                            Text(
                                category.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = FossilVaultSpacing.md)
                            )
                        }
                        items(fieldsInCategory) { field ->
                            val mapping = cfg.getMappingFor(field)
                            mapping?.let {
                                FieldMappingRow(
                                    field = field,
                                    mapping = it,
                                    availableColumns = cfg.csvResult.headers,
                                    onColumnsSelected = { columns ->
                                        viewModel.updateFieldMapping(field, columns)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldMappingRow(
    field: SpecimenField,
    mapping: FieldMapping,
    availableColumns: List<String>,
    onColumnsSelected: (List<String>) -> Unit
) {
    var showColumnPicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = FossilVaultSpacing.xs)
            .clickable { showColumnPicker = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FossilVaultSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(field.displayName, fontWeight = FontWeight.Medium)
                    if (field.isRequired) {
                        Text(
                            " *",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(
                    mapping.csvColumns.joinToString(", ").ifEmpty { "Not mapped" },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (mapping.isMapped())
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.error
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }

    if (showColumnPicker) {
        ColumnPickerDialog(
            field = field,
            availableColumns = availableColumns,
            selectedColumns = mapping.csvColumns,
            onDismiss = { showColumnPicker = false },
            onConfirm = { selected ->
                onColumnsSelected(selected)
                showColumnPicker = false
            }
        )
    }
}

@Composable
private fun ColumnPickerDialog(
    field: SpecimenField,
    availableColumns: List<String>,
    selectedColumns: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    var selected by remember { mutableStateOf(selectedColumns.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select columns for ${field.displayName}") },
        text = {
            LazyColumn {
                items(availableColumns) { column ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selected = if (column in selected) {
                                    selected - column
                                } else {
                                    selected + column
                                }
                            }
                            .padding(FossilVaultSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = column in selected,
                            onCheckedChange = null
                        )
                        Spacer(modifier = Modifier.width(FossilVaultSpacing.sm))
                        Text(column)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected.toList()) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Preview Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewContent(
    viewModel: ImportViewModel,
    onNavigateBack: () -> Unit,
    onImport: () -> Unit
) {
    val drafts by viewModel.specimenDrafts.collectAsState()
    val stats = viewModel.getPreviewStats()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preview Import") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(FossilVaultSpacing.md)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = {
                            viewModel.toggleSelectAll(stats.selected != stats.total - stats.withErrors)
                        }) {
                            Text(if (stats.selected == stats.total - stats.withErrors) "Deselect All" else "Select All")
                        }
                        Button(
                            onClick = onImport,
                            enabled = stats.selected > 0
                        ) {
                            Text("Import ${stats.selected} Specimens")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Stats cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(FossilVaultSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm)
            ) {
                StatCard("Total", stats.total.toString(), modifier = Modifier.weight(1f))
                StatCard("Selected", stats.selected.toString(), modifier = Modifier.weight(1f))
                if (stats.withErrors > 0) {
                    StatCard("Errors", stats.withErrors.toString(), modifier = Modifier.weight(1f), isError = true)
                }
                if (stats.withWarnings > 0) {
                    StatCard("Warnings", stats.withWarnings.toString(), modifier = Modifier.weight(1f), isWarning = true)
                }
            }

            // Specimen list
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(drafts) { draft ->
                    SpecimenDraftCard(
                        draft = draft,
                        onToggleSelection = { viewModel.toggleSpecimenSelection(draft.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    isWarning: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isError -> MaterialTheme.colorScheme.errorContainer
                isWarning -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(FossilVaultSpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun SpecimenDraftCard(
    draft: ImportedSpecimenDraft,
    onToggleSelection: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = FossilVaultSpacing.md, vertical = FossilVaultSpacing.xs)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FossilVaultSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (draft.canBeImported()) {
                Checkbox(
                    checked = draft.isSelected,
                    onCheckedChange = { onToggleSelection() }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(draft.getDisplayName(), fontWeight = FontWeight.Medium)
                draft.getElement()?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                if (draft.hasErrors()) {
                    Text(
                        "${draft.validationErrors.size} errors",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (draft.hasWarnings()) {
                    Text(
                        "${draft.validationWarnings.size} warnings",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (draft.hasErrors() || draft.hasWarnings()) {
                Icon(
                    if (draft.hasErrors()) Icons.Default.Error else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (draft.hasErrors())
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

/**
 * Importing Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportingContent(
    viewModel: ImportViewModel,
    onNavigateBack: () -> Unit
) {
    val progress by viewModel.importProgress.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Importing...") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(FossilVaultSpacing.lg)
            ) {
                progress?.let { prog ->
                    CircularProgressIndicator(
                        progress = { prog.progressPercentage() / 100f },
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(FossilVaultSpacing.lg))
                    Text(
                        "${prog.progressPercentage().toInt()}%",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(FossilVaultSpacing.sm))
                    Text("${prog.importedCount} of ${prog.totalSpecimens} imported")
                    prog.currentSpecimen?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

/**
 * Summary Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SummaryContent(
    viewModel: ImportViewModel,
    onDone: () -> Unit,
    onStartOver: () -> Unit
) {
    val summary by viewModel.importSummary.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Import Complete") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(FossilVaultSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            summary?.let { sum ->
                Icon(
                    if (sum.isFullSuccess()) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = if (sum.isFullSuccess())
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(FossilVaultSpacing.lg))
                Text(
                    if (sum.isFullSuccess()) "Success!" else "Partially Complete",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(FossilVaultSpacing.md))
                Text("${sum.successfullyImported} specimens imported successfully")
                if (sum.failed > 0) {
                    Text("${sum.failed} failed", color = MaterialTheme.colorScheme.error)
                }
                if (sum.skipped > 0) {
                    Text("${sum.skipped} skipped")
                }
                Spacer(modifier = Modifier.height(FossilVaultSpacing.xl))
                Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                    Text("View Collection")
                }
                OutlinedButton(onClick = onStartOver, modifier = Modifier.fillMaxWidth()) {
                    Text("Import Another File")
                }
            }
        }
    }
}

/**
 * Error Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Error") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(FossilVaultSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(FossilVaultSpacing.lg))
            Text("Import Failed", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(FossilVaultSpacing.sm))
            Text(message, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(FossilVaultSpacing.xl))
            Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                Text("Try Again")
            }
        }
    }
}
