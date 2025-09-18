package com.dmdev.fossilvaultanda.ui.screens.detail.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dmdev.fossilvaultanda.data.models.StoredImage

@Composable
fun FullScreenImageViewer(
    images: List<StoredImage>,
    currentIndex: Int,
    onDismiss: () -> Unit,
    onImageIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        BackHandler(onBack = onDismiss)

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black)
                .systemBarsPadding()
        ) {
            // Main image viewer
            ZoomableImageView(
                images = images,
                currentIndex = currentIndex,
                onImageIndexChange = onImageIndexChange,
                modifier = Modifier.fillMaxSize()
            )

            // Top bar with close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .statusBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Close image viewer",
                    tint = Color.White
                )
            }

            // Bottom thumbnail carousel
            if (images.size > 1) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                ) {
                    ImageThumbnailCarousel(
                        images = images,
                        currentIndex = currentIndex,
                        onThumbnailClick = onImageIndexChange,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}