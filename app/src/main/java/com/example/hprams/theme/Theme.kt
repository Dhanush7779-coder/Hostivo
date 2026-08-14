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
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Surface,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceContainerHigh,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

private val LuminaLightColorScheme = lightColorScheme(
    primary = Color(0xFF006A66),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF76F2EC),
    onPrimaryContainer = Color(0xFF00201E),
    secondary = Color(0xFF006E33),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF99F7AC),
    onSecondaryContainer = Color(0xFF00210A),
    tertiary = Color(0xFF435E91),
    onTertiary = Color.White,
    background = Color(0xFFF4FAF9),
    onBackground = Color(0xFF101415),
    surface = Color(0xFFF3F6F6),
    onSurface = Color(0xFF101415),
    surfaceVariant = Color(0xFFDAE5E3),
    onSurfaceVariant = Color(0xFF3F4948),
    outline = Color(0xFF6F7978),
    outlineVariant = Color(0xFFBEC9C7),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
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
