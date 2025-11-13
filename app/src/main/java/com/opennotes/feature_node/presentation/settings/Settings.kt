package com.opennotes.feature_node.presentation.settings

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class Settings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val blackTheme: Boolean = false,
    // Legacy fields - keeping for migration compatibility
    @Deprecated("Use themeMode instead") val darkTheme: Boolean = false,
    @Deprecated("Use themeMode instead") val systemTheme: Boolean = true,
    @Deprecated("Use themeMode instead") val lightTheme: Boolean = false,
)