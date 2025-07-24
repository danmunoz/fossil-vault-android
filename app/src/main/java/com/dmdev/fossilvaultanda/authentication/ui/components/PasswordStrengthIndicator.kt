package com.dmdev.fossilvaultanda.authentication.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dmdev.fossilvaultanda.R

enum class PasswordStrength(val progress: Float, val color: Color, val labelRes: Int) {
    WEAK(0.25f, Color(0xFFEF4444), R.string.auth_password_weak),
    FAIR(0.5f, Color(0xFFF59E0B), R.string.auth_password_fair),
    GOOD(0.75f, Color(0xFFFBBF24), R.string.auth_password_good),
    STRONG(1f, Color(0xFF10B981), R.string.auth_password_strong)
}

@Composable
fun PasswordStrengthIndicator(
    password: String,
    modifier: Modifier = Modifier
) {
    val strength = remember(password) { evaluatePasswordStrength(password) }
    
    val animatedProgress by animateFloatAsState(
        targetValue = strength.progress,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = 300f
        ),
        label = "password_strength_progress"
    )
    
    if (password.isNotEmpty()) {
        Column(modifier = modifier) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.auth_password_strength),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = stringResource(strength.labelRes),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = strength.color
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Gray.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(strength.color)
                )
            }
        }
    }
}

private fun evaluatePasswordStrength(password: String): PasswordStrength {
    if (password.length < 6) return PasswordStrength.WEAK
    
    var score = 0
    
    // Length bonus
    if (password.length >= 8) score += 1
    if (password.length >= 12) score += 1
    
    // Character variety
    if (password.any { it.isDigit() }) score += 1
    if (password.any { it.isUpperCase() }) score += 1
    if (password.any { it.isLowerCase() }) score += 1
    if (password.any { !it.isLetterOrDigit() }) score += 1
    
    return when {
        score >= 5 -> PasswordStrength.STRONG
        score >= 3 -> PasswordStrength.GOOD
        score >= 2 -> PasswordStrength.FAIR
        else -> PasswordStrength.WEAK
    }
}