package com.dmdev.fossilvaultanda.ui.screens.specimen.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dmdev.fossilvaultanda.data.models.StoredImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ImagePickerManager(
    maxImages: Int = 3,
    currentImages: List<StoredImage>,
    onImagesSelected: (List<StoredImage>) -> Unit,
    onPickerRequested: () -> Unit = {},
    content: @Composable (openPicker: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraPermission = permissions[Manifest.permission.CAMERA] ?: false
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false 
        } else {
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        }
        
        if (cameraPermission && storagePermission) {
            showDialog = true
        } else {
            showPermissionDialog = true
        }
    }

    // Gallery launcher (multi-select)
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val remainingSlots = maxImages - currentImages.size
        val selectedUris = uris.take(remainingSlots)
        val newImages = selectedUris.map { StoredImage(it.toString()) }
        onImagesSelected(currentImages + newImages)
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            val newImage = StoredImage(cameraImageUri.toString())
            onImagesSelected(currentImages + newImage)
        }
    }

    // Check permissions and show picker
    val openPicker = {
        onPickerRequested()
        
        val permissions = buildList {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        val hasAllPermissions = permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (hasAllPermissions) {
            showDialog = true
        } else {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    // Picker dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Photos") },
            text = { Text("Choose how to add photos to your specimen.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        val remainingSlots = maxImages - currentImages.size
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text("Gallery")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        if (currentImages.size < maxImages) {
                            cameraImageUri = createImageUri(context)
                            cameraImageUri?.let { cameraLauncher.launch(it) }
                        }
                    }
                ) {
                    Text("Camera")
                }
            }
        )
    }

    // Permission dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permissions Required") },
            text = { Text("Camera and storage permissions are needed to add photos to your specimens.") },
            confirmButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    content(openPicker)
}

private fun createImageUri(context: Context): Uri? {
    return try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "FOSSIL_${timeStamp}.jpg"
        val storageDir = File(context.getExternalFilesDir(null), "Pictures")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val imageFile = File(storageDir, imageFileName)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    } catch (e: Exception) {
        null
    }
}