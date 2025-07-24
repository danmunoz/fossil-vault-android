package com.dmdev.fossilvaultanda.ui.screens.welcome.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultRadius
import com.dmdev.fossilvaultanda.ui.theme.LogoGradientStart
import com.dmdev.fossilvaultanda.ui.theme.LogoGradientEnd
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun AnimatedLogo(
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    
    // Animation values
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "logo_rotation"
    )
    
    val shadowElevation by animateFloatAsState(
        targetValue = if (isVisible) 12f else 8f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "logo_shadow"
    )
    
    // Trigger entrance animation
    LaunchedEffect(Unit) {
        delay(100) // Staggered delay as per spec
        isVisible = true
        rotationAngle = 360f
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
    ) {
        // Logo container with gradient background
        Box(
            modifier = Modifier
                .size(90.dp)
                .scale(scale)
                .rotate(rotation)
                .shadow(
                    elevation = shadowElevation.dp,
                    shape = RoundedCornerShape(FossilVaultRadius.lg)
                )
                .clip(RoundedCornerShape(FossilVaultRadius.lg))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(LogoGradientStart, LogoGradientEnd)
                    )
                )
                .clickable {
                    // Easter egg: additional rotation on tap
                    rotationAngle += 360f
                }
                .semantics {
                    contentDescription = "FossilVault Logo - tap for animation"
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "FossilVault Logo",
                modifier = Modifier.size(45.dp),
                tint = Color.White
            )
        }
        
        // App name
        Text(
            text = "FossilVault",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        // Tagline
        Text(
            text = "Your Personal Fossil Collection, Anywhere",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = FossilVaultSpacing.md)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedLogoPreview() {
    FossilVaultTheme {
        AnimatedLogo(
            modifier = Modifier.padding(FossilVaultSpacing.lg)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedLogoDarkPreview() {
    FossilVaultTheme(darkTheme = true) {
        AnimatedLogo(
            modifier = Modifier.padding(FossilVaultSpacing.lg)
        )
    }
}