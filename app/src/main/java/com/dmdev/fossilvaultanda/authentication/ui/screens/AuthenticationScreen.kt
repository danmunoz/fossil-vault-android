package com.dmdev.fossilvaultanda.authentication.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dmdev.fossilvaultanda.R
import com.dmdev.fossilvaultanda.authentication.domain.AuthenticationMode
import com.dmdev.fossilvaultanda.authentication.ui.AuthenticationViewModel
import com.dmdev.fossilvaultanda.authentication.ui.components.AuthAnimatedLogo
import com.dmdev.fossilvaultanda.authentication.ui.components.GradientButton
import com.dmdev.fossilvaultanda.authentication.ui.components.PasswordStrengthIndicator
import com.dmdev.fossilvaultanda.authentication.ui.components.ValidatedTextField
import com.dmdev.fossilvaultanda.ui.theme.AuthBackgroundLight
import com.dmdev.fossilvaultanda.ui.theme.AuthShadowOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(
    onDismiss: () -> Unit,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    
    // Focus email field when screen appears
    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
    }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthBackgroundLight),
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.auth_close_button),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = AuthBackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Logo Section
            AuthAnimatedLogo(
                animate = !uiState.isLoading
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Header Section with Animated Content
            AnimatedContent(
                targetState = uiState.mode,
                transitionSpec = {
                    slideInVertically(
                        animationSpec = tween(300),
                        initialOffsetY = { it / 3 }
                    ) + fadeIn(animationSpec = tween(300)) togetherWith
                    slideOutVertically(
                        animationSpec = tween(300),
                        targetOffsetY = { -it / 3 }
                    ) + fadeOut(animationSpec = tween(300))
                },
                label = "header_animation"
            ) { mode ->
                HeaderSection(mode = mode)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Form Section
            AnimatedContent(
                targetState = uiState.mode,
                transitionSpec = {
                    slideInVertically(
                        animationSpec = tween(300, delayMillis = 100),
                        initialOffsetY = { it / 2 }
                    ) + fadeIn(animationSpec = tween(300, delayMillis = 100)) togetherWith
                    slideOutVertically(
                        animationSpec = tween(300),
                        targetOffsetY = { -it / 2 }
                    ) + fadeOut(animationSpec = tween(300))
                },
                label = "form_animation"
            ) { _ ->
                FormSection(
                    uiState = uiState,
                    onEmailChange = viewModel::updateEmail,
                    onPasswordChange = viewModel::updatePassword,
                    emailFocusRequester = emailFocusRequester,
                    passwordFocusRequester = passwordFocusRequester,
                    onEmailNext = { passwordFocusRequester.requestFocus() },
                    onPasswordDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (uiState.mode == AuthenticationMode.LOGIN) {
                            viewModel.signIn()
                        } else {
                            viewModel.signUp()
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Action Section
            AnimatedContent(
                targetState = uiState.mode,
                transitionSpec = {
                    slideInVertically(
                        animationSpec = tween(300, delayMillis = 200),
                        initialOffsetY = { it }
                    ) + fadeIn(animationSpec = tween(300, delayMillis = 200)) togetherWith
                    slideOutVertically(
                        animationSpec = tween(300),
                        targetOffsetY = { -it }
                    ) + fadeOut(animationSpec = tween(300))
                },
                label = "action_animation"
            ) { mode ->
                ActionSection(
                    mode = mode,
                    uiState = uiState,
                    onPrimaryAction = {
                        if (mode == AuthenticationMode.LOGIN) {
                            viewModel.signIn()
                        } else {
                            viewModel.signUp()
                        }
                    },
                    onSwitchMode = viewModel::switchMode
                )
            }
            
            // Error Message
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.errorMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HeaderSection(mode: AuthenticationMode) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(mode.titleRes),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.shadow(
                elevation = 0.dp,
                spotColor = AuthShadowOrange,
                ambientColor = AuthShadowOrange
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(mode.subtitleRes),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun FormSection(
    uiState: com.dmdev.fossilvaultanda.authentication.ui.AuthenticationUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    emailFocusRequester: FocusRequester,
    passwordFocusRequester: FocusRequester,
    onEmailNext: () -> Unit,
    onPasswordDone: () -> Unit
) {
    Column {
        ValidatedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.auth_email_label),
            placeholder = stringResource(R.string.auth_email_placeholder),
            isError = uiState.emailError != null,
            errorMessage = uiState.emailError,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            onImeAction = onEmailNext,
            focusRequester = emailFocusRequester
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        ValidatedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.auth_password_label),
            placeholder = stringResource(R.string.auth_password_placeholder),
            isError = uiState.passwordError != null,
            errorMessage = uiState.passwordError,
            isPassword = true,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            onImeAction = onPasswordDone,
            focusRequester = passwordFocusRequester
        )
        
        // Password Strength Indicator (only for sign up)
        if (uiState.mode == AuthenticationMode.SIGN_UP) {
            Spacer(modifier = Modifier.height(12.dp))
            PasswordStrengthIndicator(
                password = uiState.password
            )
        }
    }
}

@Composable
private fun ActionSection(
    mode: AuthenticationMode,
    uiState: com.dmdev.fossilvaultanda.authentication.ui.AuthenticationUiState,
    onPrimaryAction: () -> Unit,
    onSwitchMode: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GradientButton(
            text = stringResource(mode.primaryButtonRes),
            onClick = onPrimaryAction,
            enabled = uiState.isFormValid && !uiState.isTransitioning,
            isLoading = uiState.isLoading,
            icon = mode.primaryButtonIcon
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(mode.switchPromptRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = stringResource(mode.switchActionRes),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(
                    enabled = !uiState.isLoading && !uiState.isTransitioning,
                    onClick = onSwitchMode
                )
            )
        }
    }
}