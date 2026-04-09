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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    private var hasShownBiometric = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            !settingsViewModel.isLoaded.value

        }



        setContent {
            val currentSettings by settingsViewModel.settings.collectAsState()

            OpenNotesTheme(settings = currentSettings) {
                Surface(color = MaterialTheme.colorScheme.background) {
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
                                ThemeMode.SYSTEM ->
                                    isSystemInDarkTheme()
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
                            AboutScreen(
                                navController = navController
                            )
                        }

                    }
                }
            }
        }


    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !hasShownBiometric) {
            hasShownBiometric = true
            handleBiometricLock()
        }
    }

    private fun handleBiometricLock() {
        lifecycleScope.launch {
            settingsViewModel.isLoaded.filter { it }.first()
            if (!settingsViewModel.settings.value.biometricLock) return@launch

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock OpenNotes")
                .setSubtitle("Confirm your fingerprint to access your notes")
                .setNegativeButtonText("Cancel")
                .build()

            BiometricPrompt(
                this@MainActivity,
                ContextCompat.getMainExecutor(this@MainActivity),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) = Unit
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) = finish()
                    override fun onAuthenticationFailed() = Unit
                }
            ).authenticate(promptInfo)
        }
    }
}