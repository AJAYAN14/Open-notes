package com.opennotes.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    settings: Settings,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemInDarkTheme = isSystemInDarkTheme()

    val darkTheme = when {
        settings.lightTheme -> systemInDarkTheme
        else -> settings.darkTheme
    }


    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (darkTheme) darkColorScheme() else lightColorScheme()
    }

    android.util.Log.d("DynamicColors", "Using dynamic colors on Android ${Build.VERSION.SDK_INT}")

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}