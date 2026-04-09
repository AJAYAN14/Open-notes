package com.opennotes.feature_node.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.opennotes.feature_node.presentation.add_edit_note.AddEditNoteScreen
import com.opennotes.ui.theme.NoteColorPalette
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.opennotes.feature_node.presentation.notes.NotesScreen
import com.opennotes.feature_node.presentation.settings.AboutScreen
import com.opennotes.feature_node.presentation.settings.SettingsScreen
import com.opennotes.feature_node.presentation.settings.SettingsViewModel
import com.opennotes.feature_node.presentation.settings.ThemeMode
import com.opennotes.feature_node.presentation.util.Screen
import com.opennotes.ui.theme.OpenNotesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { !settingsViewModel.isLoaded.value }

        setContent {
            val currentSettings by settingsViewModel.settings.collectAsState()
            val isAppUnlocked by settingsViewModel.isAppUnlocked.collectAsState()
            val isLoaded by settingsViewModel.isLoaded.collectAsState()

            var showContent by remember { mutableStateOf(false) }

            LaunchedEffect(isAppUnlocked, currentSettings.biometricLock, isLoaded) {
                if (!isLoaded) return@LaunchedEffect  // wait for real settings to load
                when {
                    !currentSettings.biometricLock || isAppUnlocked -> showContent = true
                    else -> {
                        showBiometricPrompt(
                            onSuccess = {
                                settingsViewModel.setAppUnlocked(true)
                                showContent = true
                            },
                            onError = { errorCode, errString ->
                                handleBiometricError(errorCode, errString) {
                                    showContent = true
                                }
                            }
                        )
                    }
                }
            }

            OpenNotesTheme(settings = currentSettings) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    if (showContent) {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = Screen.NotesScreen.route,
                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None },
                            popEnterTransition = { EnterTransition.None },
                            popExitTransition = { ExitTransition.None }
                        ) {
                            composable(route = Screen.NotesScreen.route) {
                                NotesScreen(navController = navController)
                            }
                            composable(
                                route = Screen.AddEditNoteScreen.route +
                                        "?noteId={noteId}&noteColor={noteColor}",
                                arguments = listOf(
                                    navArgument("noteId") {
                                        type = NavType.IntType
                                        defaultValue = -1
                                    },
                                    navArgument("noteColor") {
                                        type = NavType.IntType
                                        defaultValue = -1
                                    }
                                )
                            ) { backStackEntry ->
                                val color = backStackEntry.arguments?.getInt("noteColor")
                                    ?.takeIf { it != -1 }
                                val isDarkTheme = when (currentSettings.themeMode) {
                                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                                    ThemeMode.LIGHT -> false
                                    ThemeMode.DARK -> true
                                }
                                val resolvedColor = color ?: if (isDarkTheme) {
                                    NoteColorPalette.Dark.first().toArgb()
                                } else {
                                    NoteColorPalette.Light.first().toArgb()
                                }
                                AddEditNoteScreen(
                                    navController = navController,
                                    noteColor = resolvedColor,
                                    isDarkTheme = isDarkTheme
                                )
                            }
                            composable(route = Screen.SettingsScreen.route) {
                                SettingsScreen(
                                    navController = navController,
                                    viewModel = settingsViewModel
                                )
                            }
                            composable(route = Screen.AboutScreen.route) {
                                AboutScreen(navController = navController)
                            }
                        }
                    } else {
                        // Blank surface shown until auth completes
                        Surface(color = MaterialTheme.colorScheme.background) {}
                    }
                }
            }
        }
    }

    private fun showBiometricPrompt(
        onSuccess: () -> Unit,
        onError: (Int, CharSequence) -> Unit
    ) {
        BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onError(errorCode, errString)
                }
                override fun onAuthenticationFailed() = Unit
            }
        ).authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock OpenNotes")
                .setSubtitle("Confirm your fingerprint to access your notes")
                .setNegativeButtonText("Cancel")
                .build()
        )
    }

    private fun handleBiometricError(
        errorCode: Int,
        errString: CharSequence,
        onComplete: () -> Unit
    ) {
        when (errorCode) {
            BiometricPrompt.ERROR_USER_CANCELED,
            BiometricPrompt.ERROR_NEGATIVE_BUTTON,
            BiometricPrompt.ERROR_CANCELED -> finish()

            BiometricPrompt.ERROR_NO_BIOMETRICS,
            BiometricPrompt.ERROR_HW_NOT_PRESENT,
            BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                settingsViewModel.setAppUnlocked(true)
                // disable biometric lock since hardware is unavailable
                settingsViewModel.onBiometricLockToggleRequest(false)
                onComplete()
            }

            else -> finish()
        }
    }
}