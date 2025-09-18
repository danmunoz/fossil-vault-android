package com.dmdev.fossilvaultanda.ui.screens.detail.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dmdev.fossilvaultanda.data.models.StoredImage
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoomableImageView(
    images: List<StoredImage>,
    currentIndex: Int,
    onImageIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = currentIndex,
        pageCount = { images.size }
    )

    // Sync pager state with external index
    LaunchedEffect(currentIndex) {
        if (pagerState.currentPage != currentIndex) {
            pagerState.animateScrollToPage(currentIndex)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != currentIndex) {
            onImageIndexChange(pagerState.currentPage)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        val image = images[page]

        ZoomableImage(
            image = image,
            contentDescription = "Fossil image ${page + 1} of ${images.size}",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ZoomableImage(
    image: StoredImage,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(image.url)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // Apply zoom with limits
                    val newScale = (scale * zoom).coerceIn(0.5f, 3f)
                    scale = newScale

                    // Apply pan with constraints
                    val maxX = (size.width * (scale - 1)) / 2
                    val maxY = (size.height * (scale - 1)) / 2

                    offset = Offset(
                        x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                        y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                    )
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .semantics {
                this.contentDescription = "$contentDescription, pinch to zoom, drag to pan"
            },
        contentScale = ContentScale.Fit
    )
}