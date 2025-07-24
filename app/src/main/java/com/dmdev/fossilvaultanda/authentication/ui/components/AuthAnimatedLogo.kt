package com.dmdev.fossilvaultanda.authentication.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.ui.theme.LogoGradientEnd
import com.dmdev.fossilvaultanda.ui.theme.LogoGradientStart

@Composable
fun AuthAnimatedLogo(
    modifier: Modifier = Modifier,
    animate: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_animation")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (animate) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = if (animate) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .size(120.dp)
            .scale(0.6f)
            .semantics { contentDescription = "FossilVault logo" },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(120.dp)
                .rotate(rotation)
                .scale(scale)
        ) {
            drawAnimatedLogo()
        }
    }
}

private fun DrawScope.drawAnimatedLogo() {
    val canvasWidth = size.width
    val canvasHeight = size.height
    val center = Offset(canvasWidth / 2, canvasHeight / 2)
    val diamondSize = minOf(canvasWidth, canvasHeight) * 0.8f
    
    // Create orange diamond background
    val outerDiamondPath = Path().apply {
        moveTo(center.x, center.y - diamondSize / 2)
        lineTo(center.x + diamondSize / 2, center.y)
        lineTo(center.x, center.y + diamondSize / 2)
        lineTo(center.x - diamondSize / 2, center.y)
        close()
    }
    
    // Create white diamond inner shape
    val innerDiamondSize = diamondSize * 0.6f
    val innerDiamondPath = Path().apply {
        moveTo(center.x, center.y - innerDiamondSize / 2)
        lineTo(center.x + innerDiamondSize / 2, center.y)
        lineTo(center.x, center.y + innerDiamondSize / 2)
        lineTo(center.x - innerDiamondSize / 2, center.y)
        close()
    }
    
    // Draw outer diamond with gradient
    val gradient = Brush.linearGradient(
        colors = listOf(LogoGradientStart, LogoGradientEnd),
        start = Offset(center.x - diamondSize / 2, center.y - diamondSize / 2),
        end = Offset(center.x + diamondSize / 2, center.y + diamondSize / 2)
    )
    
    drawPath(
        path = outerDiamondPath,
        brush = gradient
    )
    
    // Draw inner white diamond
    drawPath(
        path = innerDiamondPath,
        color = androidx.compose.ui.graphics.Color.White
    )
}