package org.hyperhawks.itantra.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Transparent

private val TacticalColorScheme: ColorScheme = darkColorScheme(
  primary = TacticalOrangePrimary,
  onPrimary = TacticalBackground,
  primaryContainer = TacticalOrangeDark,
  onPrimaryContainer = TextPrimary,
  secondary = TacticalCyan,
  onSecondary = TacticalBackground,
  secondaryContainer = TacticalCyanDark,
  onSecondaryContainer = TextPrimary,
  tertiary = TacticalAmber,
  background = TacticalBackground,
  onBackground = TextPrimary,
  surface = TacticalSurface,
  onSurface = TextPrimary,
  surfaceVariant = TacticalSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = TacticalBorder,
  error = TacticalRed,
  onError = TextPrimary
)

@Composable
fun ITantraTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = TacticalColorScheme,
    typography = Typography,
    content = content
  )
}
