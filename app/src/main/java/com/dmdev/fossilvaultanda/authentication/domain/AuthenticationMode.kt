package com.dmdev.fossilvaultanda.authentication.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.dmdev.fossilvaultanda.R

enum class AuthenticationMode(
    val titleRes: Int,
    val subtitleRes: Int,
    val primaryButtonRes: Int,
    val primaryButtonIcon: ImageVector,
    val switchPromptRes: Int,
    val switchActionRes: Int
) {
    LOGIN(
        titleRes = R.string.auth_welcome_back,
        subtitleRes = R.string.auth_signin_subtitle,
        primaryButtonRes = R.string.auth_sign_in,
        primaryButtonIcon = Icons.AutoMirrored.Filled.ArrowForward,
        switchPromptRes = R.string.auth_no_account,
        switchActionRes = R.string.auth_create_account
    ),
    SIGN_UP(
        titleRes = R.string.auth_get_started,
        subtitleRes = R.string.auth_signup_subtitle,
        primaryButtonRes = R.string.auth_create_account,
        primaryButtonIcon = Icons.Filled.Person,
        switchPromptRes = R.string.auth_have_account,
        switchActionRes = R.string.auth_sign_in
    )
}