package com.dmdev.fossilvaultanda.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GradientPrimaryStartDark,
    secondary = AccentGreenDark,
    tertiary = AccentBlueDark,
    background = BackgroundPrimaryDark,
    surface = BackgroundCardDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    primaryContainer = BackgroundSecondaryDark,
    onPrimaryContainer = TextPrimaryDark,
    secondaryContainer = BackgroundInputDark,
    onSecondaryContainer = TextSecondaryDark,
    outline = BorderDark,
    error = TextError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = GradientPrimaryStartLight,
    secondary = AccentGreenLight,
    tertiary = AccentBlueLight,
    background = BackgroundPrimaryLight,
    surface = BackgroundCardLight,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    primaryContainer = BackgroundSecondaryLight,
    onPrimaryContainer = TextPrimaryLight,
    secondaryContainer = BackgroundInputLight,
    onSecondaryContainer = TextSecondaryLight,
    outline = BorderLight,
    error = TextError,
    onError = Color.White
)

@Composable
fun FossilVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}