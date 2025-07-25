package com.dmdev.fossilvaultanda.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationState
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun AccountManagementSection(
    authenticationState: AuthenticationState,
    onCreateAccountClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.sm)
    ) {
        when (authenticationState) {
            AuthenticationState.AUTHENTICATED -> {
                // Sign Out button for authenticated users
                TextButton(
                    onClick = onSignOutClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "Sign Out",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            AuthenticationState.LOCAL_USER -> {
                // Create Account button for anonymous users
                Button(
                    onClick = onCreateAccountClick,
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
                    Text(text = "Create Account")
                }
                
                // Delete Account button for anonymous users
                TextButton(
                    onClick = onDeleteAccountClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "Delete Account",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            else -> {
                // For unauthenticated state, show create account
                Button(
                    onClick = onCreateAccountClick,
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
                    Text(text = "Create Account")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountManagementSectionAuthenticatedPreview() {
    FossilVaultTheme {
        AccountManagementSection(
            authenticationState = AuthenticationState.AUTHENTICATED,
            onCreateAccountClick = {},
            onSignOutClick = {},
            onDeleteAccountClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountManagementSectionAnonymousPreview() {
    FossilVaultTheme {
        AccountManagementSection(
            authenticationState = AuthenticationState.LOCAL_USER,
            onCreateAccountClick = {},
            onSignOutClick = {},
            onDeleteAccountClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}