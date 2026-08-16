package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HighDensityDarkColorScheme =
  darkColorScheme(
    primary = HighDensityPrimary,
    onPrimary = HighDensityOnPrimary,
    primaryContainer = HighDensityPrimaryContainer,
    onPrimaryContainer = HighDensityOnPrimaryContainer,
    secondary = HighDensitySecondaryContainer,
    onSecondary = HighDensityOnSecondaryContainer,
    secondaryContainer = HighDensitySecondaryContainer,
    onSecondaryContainer = HighDensityOnSecondaryContainer,
    tertiary = HighDensityGreen,
    onTertiary = DisplayBlack,
    background = DisplayBlack,
    onBackground = TextPrimary,
    surface = DisplayDarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DisplayCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = DisplayBorder,
    outlineVariant = DisplayBorder,
    error = RoseError,
    onError = DisplayBlack
  )

@Composable
fun DisplayMasterTheme(
  content: @Composable () -> Unit,
) {
  val colorScheme = HighDensityDarkColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = DisplayBlack.toArgb()
        window.navigationBarColor = DisplayBlack.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

