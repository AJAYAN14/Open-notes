package com.opennotes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.material.color.utilities.DynamicColor
import com.opennotes.feature_node.presentation.settings.Settings


// AMOLED Color Scheme
private val AmoledColorScheme = darkColorScheme(
    primary = Color.White,
    background = PureBlack,
    onBackground = Color.White,
    surface = PureBlack,
    onSurface = Color.White
)

// Light theme
private val LightColorScheme = lightColorScheme(
    primary = DarkGray,
    background = Color.White,
    onBackground = DarkGray,
    surface = Color.LightGray,
    onSurface = Color.Black
)

private val AppTypography = Apptypography
private val AppShapes = Shapes()

@Composable
fun OpenNotesTheme(
    settings: Settings = Settings(),
    content: @Composable () -> Unit
) {
    val systemInDarkTheme = isSystemInDarkTheme()


    val darkTheme = when {
        settings.automaticTheme -> systemInDarkTheme
        else -> settings.darkTheme
    }

    val colorScheme = if (darkTheme) AmoledColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}