package com.example.hprams.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object ThemeManager {
    var themeSetting by mutableStateOf("System") // "System", "Dark", "Light"
}

@Composable
fun isAppDarkTheme(): Boolean {
    return when (ThemeManager.themeSetting) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemInDarkTheme()
    }
}

private val LuminaDarkColorScheme = darkColorScheme(
    primary = AccentColor,
    onPrimary = Color.White,
    primaryContainer = AccentColorDark,
    onPrimaryContainer = Color.White,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = DarkBg,
    onBackground = DarkTextColor,
    surface = DarkBg,
    onSurface = DarkTextColor,
    surfaceVariant = DarkBg,
    onSurfaceVariant = DarkSubTextColor,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

private val LuminaLightColorScheme = lightColorScheme(
    primary = AccentColor,
    onPrimary = Color.White,
    primaryContainer = AccentColor,
    onPrimaryContainer = Color.White,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    background = LightBg,
    onBackground = LightTextColor,
    surface = LightBg,
    onSurface = LightTextColor,
    surfaceVariant = LightBg,
    onSurfaceVariant = LightSubTextColor,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

@Composable
fun HPRAMSTheme(
  content: @Composable () -> Unit,
) {
  val isDark = isAppDarkTheme()
  MaterialTheme(
    colorScheme = if (isDark) LuminaDarkColorScheme else LuminaLightColorScheme,
    typography = Typography,
    content = content
  )
}
