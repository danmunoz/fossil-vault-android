package com.dmdev.fossilvaultanda.ui.screens.welcome.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
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
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTouchTarget
import com.dmdev.fossilvaultanda.ui.theme.GradientPrimaryStartLight
import com.dmdev.fossilvaultanda.ui.theme.GradientPrimaryEnd
import com.dmdev.fossilvaultanda.ui.theme.BackgroundSecondaryLight
import com.dmdev.fossilvaultanda.ui.theme.BorderLight
import com.dmdev.fossilvaultanda.ui.theme.TextSecondaryLight
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun CTASection(
    onStartBuildingClick: () -> Unit = {},
    onTryWithoutAccountClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    // Trigger entrance animation
    LaunchedEffect(Unit) {
        delay(800) // Staggered delay as per spec
        isVisible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cta_alpha"
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
    ) {
        // Primary button
        AnimatedButton(
            onClick = onStartBuildingClick,
            delay = 100,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onStartBuildingClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FossilVaultTouchTarget.preferred)
                    .gradientBackground()
                    .semantics {
                        contentDescription = "Start building your fossil collection with an account"
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(FossilVaultRadius.lg)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Start Building Your Collection",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
        
        // Divider with "or" text
        AnimatedButton(
            onClick = {},
            delay = 200,
            modifier = Modifier.fillMaxWidth()
        ) {
            DividerWithText(text = "or")
        }
        
        // Secondary button
        AnimatedButton(
            onClick = onTryWithoutAccountClick,
            delay = 300,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onTryWithoutAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FossilVaultTouchTarget.preferred)
                    .semantics {
                        contentDescription = "Try without account - start cataloging immediately without signup"
                    },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = BackgroundSecondaryLight,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(BorderLight, BorderLight))
                ),
                shape = RoundedCornerShape(FossilVaultRadius.lg)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Try Without Account",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        
        // Footer text
        AnimatedButton(
            onClick = {},
            delay = 400,
            modifier = Modifier
        ) {
            Text(
                text = "No signup required • Start cataloging immediately",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = FossilVaultSpacing.sm)
            )
        }
    }
}

@Composable
private fun AnimatedButton(
    onClick: () -> Unit,
    delay: Long,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(delay)
        isVisible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_alpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_scale"
    )
    
    Box(
        modifier = modifier
            .alpha(alpha)
            .scale(scale)
    ) {
        content()
    }
}

@Composable
private fun DividerWithText(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(BorderLight)
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryLight
        )
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(BorderLight)
        )
    }
}

private fun Modifier.gradientBackground(): Modifier = composed {
    background(
        brush = Brush.linearGradient(
            colors = listOf(GradientPrimaryStartLight, GradientPrimaryEnd)
        ),
        shape = RoundedCornerShape(FossilVaultRadius.lg)
    )
}

@Preview(showBackground = true)
@Composable
fun CTASectionPreview() {
    FossilVaultTheme {
        CTASection(
            modifier = Modifier.padding(FossilVaultSpacing.lg)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CTASectionDarkPreview() {
    FossilVaultTheme(darkTheme = true) {
        CTASection(
            modifier = Modifier.padding(FossilVaultSpacing.lg)
        )
    }
}