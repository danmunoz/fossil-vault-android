package com.dmdev.fossilvaultanda.ui.screens.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationState
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultRadius
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun ActionCardsSection(
    authenticationState: AuthenticationState,
    subscriptionBadge: String?,
    onNavigateToEditProfile: () -> Unit,
    onSubscriptionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAnonymous = authenticationState == AuthenticationState.LOCAL_USER

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm)
    ) {
        // Edit Profile Card
        ActionCard(
            icon = Icons.Default.Edit,
            iconColor = MaterialTheme.colorScheme.primary,
            title = "Edit Profile",
            badge = null,
            onClick = onNavigateToEditProfile,
            enabled = !isAnonymous
        )

        // Subscription Card
        ActionCard(
            icon = Icons.Default.Star,
            iconColor = Color(0xFFFFD700), // Gold color for crown
            title = "Subscription",
            badge = subscriptionBadge,
            onClick = onSubscriptionClick,
            enabled = !isAnonymous
        )
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    badge: String?,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .alpha(if (enabled) 1f else 0.5f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(FossilVaultSpacing.cardInternal)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(FossilVaultRadius.md),
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(FossilVaultSpacing.md))
            
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            
            // Badge (if present)
            badge?.let {
                Surface(
                    shape = RoundedCornerShape(FossilVaultRadius.sm),
                    color = Color(0xFF4CAF50) // Green for PRO badge
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = FossilVaultSpacing.sm,
                            vertical = FossilVaultSpacing.xs
                        )
                    )
                }
                
                Spacer(modifier = Modifier.width(FossilVaultSpacing.sm))
            }
            
            // Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionCardsSectionPreview() {
    FossilVaultTheme {
        ActionCardsSection(
            authenticationState = AuthenticationState.AUTHENTICATED,
            subscriptionBadge = "FREE",
            onNavigateToEditProfile = {},
            onSubscriptionClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ActionCardsSectionAnonymousPreview() {
    FossilVaultTheme {
        ActionCardsSection(
            authenticationState = AuthenticationState.LOCAL_USER,
            subscriptionBadge = null,
            onNavigateToEditProfile = {},
            onSubscriptionClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}