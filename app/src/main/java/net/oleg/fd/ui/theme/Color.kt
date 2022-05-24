package net.oleg.fd.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle

// The colors were generated using
//  https://material-foundation.github.io/material-theme-builder/#/custom

val md_theme_light_primary = Color(0xFF006c4a)
val md_theme_light_onPrimary = Color(0xFFffffff)
val md_theme_light_primaryContainer = Color(0xFF89f8c7)
val md_theme_light_onPrimaryContainer = Color(0xFF002114)
val md_theme_light_secondary = Color(0xFF4d6357)
val md_theme_light_onSecondary = Color(0xFFffffff)
val md_theme_light_secondaryContainer = Color(0xFFcfe8d9)
val md_theme_light_onSecondaryContainer = Color(0xFF0a1f16)
val md_theme_light_tertiary = Color(0xFF3d6373)
val md_theme_light_onTertiary = Color(0xFFffffff)
val md_theme_light_tertiaryContainer = Color(0xFFc0e8fb)
val md_theme_light_onTertiaryContainer = Color(0xFF001f2a)
val md_theme_light_error = Color(0xFFba1b1b)
val md_theme_light_errorContainer = Color(0xFFffdad4)
val md_theme_light_onError = Color(0xFFffffff)
val md_theme_light_onErrorContainer = Color(0xFF410001)
val md_theme_light_background = Color(0xFFfbfdf9)
val md_theme_light_onBackground = Color(0xFF191c1a)
val md_theme_light_surface = Color(0xFFfbfdf9)
val md_theme_light_onSurface = Color(0xFF191c1a)
val md_theme_light_surfaceVariant = Color(0xFFdbe5dd)
val md_theme_light_onSurfaceVariant = Color(0xFF404943)
val md_theme_light_outline = Color(0xFF707973)
val md_theme_light_inverseOnSurface = Color(0xFFeff1ed)
val md_theme_light_inverseSurface = Color(0xFF2d312e)
val md_theme_light_inversePrimary = Color(0xFF6cdbac)
val md_theme_light_shadow = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFF6cdbac)
val md_theme_dark_onPrimary = Color(0xFF003825)
val md_theme_dark_primaryContainer = Color(0xFF005237)
val md_theme_dark_onPrimaryContainer = Color(0xFF89f8c7)
val md_theme_dark_secondary = Color(0xFFb3ccbd)
val md_theme_dark_onSecondary = Color(0xFF1f352b)
val md_theme_dark_secondaryContainer = Color(0xFF354b40)
val md_theme_dark_onSecondaryContainer = Color(0xFFcfe8d9)
val md_theme_dark_tertiary = Color(0xFFa4ccde)
val md_theme_dark_onTertiary = Color(0xFF083544)
val md_theme_dark_tertiaryContainer = Color(0xFF254c5b)
val md_theme_dark_onTertiaryContainer = Color(0xFFc0e8fb)
val md_theme_dark_error = Color(0xFFffb4a9)
val md_theme_dark_errorContainer = Color(0xFF930006)
val md_theme_dark_onError = Color(0xFF680003)
val md_theme_dark_onErrorContainer = Color(0xFFffdad4)
val md_theme_dark_background = Color(0xFF191c1a)
val md_theme_dark_onBackground = Color(0xFFe1e3df)
val md_theme_dark_surface = Color(0xFF191c1a)
val md_theme_dark_onSurface = Color(0xFFe1e3df)
val md_theme_dark_surfaceVariant = Color(0xFF404943)
val md_theme_dark_onSurfaceVariant = Color(0xFFc0c9c2)
val md_theme_dark_outline = Color(0xFF89938c)
val md_theme_dark_inverseOnSurface = Color(0xFF191c1a)
val md_theme_dark_inverseSurface = Color(0xFFe1e3df)
val md_theme_dark_inversePrimary = Color(0xFF006c4a)
val md_theme_dark_shadow = Color(0xFF000000)

val seed = Color(0xFF37a97e)
val error = Color(0xFFba1b1b)

@Composable
fun invertedButtonColors(): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = colorScheme.inverseOnSurface,
        contentColor = colorScheme.primary
    )

@Composable
fun deleteButtonColors(): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = colorScheme.errorContainer,
        contentColor = colorScheme.error
    )

@Composable
fun requestBarcodeSpanStyle(): SpanStyle =
    SpanStyle(
        color = colorScheme.secondary,
        background = colorScheme.secondaryContainer
    )
