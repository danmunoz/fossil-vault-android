package com.dmdev.fossilvaultanda.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultRadius
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultSpacing
import com.dmdev.fossilvaultanda.ui.theme.FossilVaultTheme

@Composable
fun ProfileInformationSection(
    name: String,
    onNameChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FossilVaultSpacing.cardInternal),
            verticalArrangement = Arrangement.spacedBy(FossilVaultSpacing.md)
        ) {
            // Section Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = RoundedCornerShape(FossilVaultRadius.sm),
                    color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.padding(4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(FossilVaultSpacing.sm))
                
                Text(
                    text = "Profile Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Name Field
            Column {
                Text(
                    text = "Name",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = FossilVaultSpacing.xs)
                )
                
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your name") },
                    singleLine = true,
                    shape = RoundedCornerShape(FossilVaultRadius.input)
                )
            }
            
            // Location Field
            Column {
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = FossilVaultSpacing.xs)
                )
                
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your location") },
                    singleLine = true,
                    shape = RoundedCornerShape(FossilVaultRadius.input)
                )
            }
            
            // Bio Field
            Column {
                Text(
                    text = "Bio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = FossilVaultSpacing.xs)
                )
                
                OutlinedTextField(
                    value = bio,
                    onValueChange = onBioChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tell us about yourself") },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(FossilVaultRadius.input)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileInformationSectionPreview() {
    FossilVaultTheme {
        ProfileInformationSection(
            name = "Daniel Munoz",
            onNameChange = {},
            location = "Berlin",
            onLocationChange = {},
            bio = "Testing",
            onBioChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}