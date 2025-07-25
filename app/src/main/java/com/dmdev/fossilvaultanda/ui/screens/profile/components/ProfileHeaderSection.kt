package com.dmdev.fossilvaultanda.ui.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationState
import com.dmdev.fossilvaultanda.data.models.UserProfile
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun ProfileHeaderSection(
    profile: UserProfile?,
    authenticationState: AuthenticationState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        ProfileImage(
            imageUrl = profile?.picture?.url,
            authenticationState = authenticationState,
            size = 100.dp
        )
        
        Spacer(modifier = Modifier.height(FossilVaultSpacing.md))
        
        // User Name
        Text(
            text = when (authenticationState) {
                AuthenticationState.AUTHENTICATED -> {
                    profile?.fullName ?: profile?.username ?: "Fossil Enthusiast"
                }
                AuthenticationState.LOCAL_USER -> "Fossil Enthusiast"
                else -> "Not signed in"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        // User Email (for authenticated users)
        if (authenticationState == AuthenticationState.AUTHENTICATED && profile?.email != null) {
            Spacer(modifier = Modifier.height(FossilVaultSpacing.xs))
            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
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
                    AuthenticationState.AUTHENTICATED -> MaterialTheme.colorScheme.primary
                    AuthenticationState.LOCAL_USER -> Color(0xFF2196F3) // Blue for anonymous
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
                    AuthenticationState.AUTHENTICATED -> MaterialTheme.colorScheme.onPrimary
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
fun ProfileHeaderSectionAuthenticatedPreview() {
    FossilVaultTheme {
        ProfileHeaderSection(
            profile = UserProfile(
                userId = "1",
                email = "test@me.com",
                fullName = "Daniel Munoz"
            ),
            authenticationState = AuthenticationState.AUTHENTICATED
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileHeaderSectionAnonymousPreview() {
    FossilVaultTheme {
        ProfileHeaderSection(
            profile = null,
            authenticationState = AuthenticationState.LOCAL_USER
        )
    }
}