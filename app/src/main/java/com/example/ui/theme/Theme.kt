package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
  primary = ForestGreenPrimary,
  onPrimary = Color.White,
  primaryContainer = ForestGreenContainer,
  onPrimaryContainer = OnForestGreenContainer,
  secondary = GhanaGoldAccent,
  onSecondary = Color.Black,
  secondaryContainer = GoldContainer,
  onSecondaryContainer = OnGoldContainer,
  tertiary = ForestGreenLight,
  onTertiary = Color.White,
  background = BackgroundLight,
  onBackground = CharcoalText,
  surface = SurfaceLight,
  onSurface = CharcoalText,
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = CharcoalSubtle,
  outline = CharcoalBorder
)

private val DarkColorScheme = darkColorScheme(
  primary = ForestGreenLight,
  onPrimary = Color.White,
  primaryContainer = ForestGreenDark,
  onPrimaryContainer = ForestGreenContainer,
  secondary = GhanaGoldAccent,
  onSecondary = Color.Black,
  secondaryContainer = GhanaGoldDark,
  onSecondaryContainer = GoldContainer,
  tertiary = GhanaGoldLight,
  onTertiary = Color.Black,
  background = BackgroundDark,
  onBackground = TextDarkPrimary,
  surface = SurfaceDark,
  onSurface = TextDarkPrimary,
  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = TextDarkSubtle,
  outline = CharcoalSubtle
)

@Composable
fun GhanaGrantFinderTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = colorScheme.primary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
