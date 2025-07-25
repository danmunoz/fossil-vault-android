package com.dmdev.fossilvaultanda.ui.screens.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationState
import com.dmdev.fossilvaultanda.data.models.UserProfile
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultRadius
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun ProfileSection(
    profile: UserProfile?,
    authenticationState: AuthenticationState,
    onNavigateToProfile: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // Profile preview card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToProfile() },
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
                // Profile image
                ProfileImage(
                    imageUrl = profile?.picture?.url,
                    authenticationState = authenticationState,
                    size = 50.dp
                )
                
                Spacer(modifier = Modifier.width(FossilVaultSpacing.md))
                
                // Profile info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = when (authenticationState) {
                            AuthenticationState.AUTHENTICATED -> profile?.email ?: "Unknown User"
                            AuthenticationState.LOCAL_USER -> "Anonymous user"
                            else -> "Not signed in"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = when (authenticationState) {
                            AuthenticationState.AUTHENTICATED -> "Account, Subscriptions, and more"
                            AuthenticationState.LOCAL_USER -> "Not signed in"
                            else -> "Sign in to access all features"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Arrow icon
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Create Account button for anonymous users
        if (authenticationState == AuthenticationState.LOCAL_USER || authenticationState == AuthenticationState.UNAUTHENTICATED) {
            Spacer(modifier = Modifier.height(FossilVaultSpacing.md))
            
            Button(
                onClick = onNavigateToAuth,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(FossilVaultSpacing.sm))
                Text("Create Account")
            }
        }
    }
}

@Composable
private fun ProfileImage(
    imageUrl: String?,
    authenticationState: AuthenticationState,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                when (authenticationState) {
                    AuthenticationState.AUTHENTICATED -> MaterialTheme.colorScheme.primaryContainer
                    AuthenticationState.LOCAL_USER -> Color(0xFFFF9800) // Orange for anonymous
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile picture",
                modifier = Modifier.size(size),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile picture",
                tint = when (authenticationState) {
                    AuthenticationState.AUTHENTICATED -> MaterialTheme.colorScheme.onPrimaryContainer
                    AuthenticationState.LOCAL_USER -> Color.White
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSectionAuthenticatedPreview() {
    FossilVaultTheme {
        ProfileSection(
            profile = UserProfile(
                userId = "1",
                email = "test@me.com",
                fullName = "Daniel Munoz"
            ),
            authenticationState = AuthenticationState.AUTHENTICATED,
            onNavigateToProfile = {},
            onNavigateToAuth = {},
            onSignOut = {},
            onDeleteAccount = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileSectionAnonymousPreview() {
    FossilVaultTheme {
        ProfileSection(
            profile = null,
            authenticationState = AuthenticationState.LOCAL_USER,
            onNavigateToProfile = {},
            onNavigateToAuth = {},
            onSignOut = {},
            onDeleteAccount = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}