package com.opennotes.feature_node.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.opennotes.feature_node.presentation.add_edit_note.AddEditNoteScreen
import com.opennotes.feature_node.presentation.notes.NotesScreen
import com.opennotes.feature_node.presentation.settings.SettingsScreen
import com.opennotes.feature_node.presentation.settings.SettingsViewModel
import com.opennotes.feature_node.presentation.util.Screen
import com.opennotes.ui.theme.OpenNotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            !settingsViewModel.isLoaded.value
        }

        setContent {
            val currentSettings by settingsViewModel.settings.collectAsState()


            LaunchedEffect(currentSettings) {
                android.util.Log.d("MainActivity",
                    "Settings: systemTheme=${currentSettings.systemTheme}, " +
                            "darkTheme=${currentSettings.darkTheme}, " +
                            "lightTheme=${currentSettings.lightTheme}")
            }

            OpenNotesTheme(settings = currentSettings) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.NotesScreen.route
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
                            val color = backStackEntry.arguments?.getInt("noteColor") ?: -1
                            AddEditNoteScreen(
                                navController = navController,
                                noteColor = color
                            )
                        }
                        composable(route = Screen.SettingsScreen.route) {
                            SettingsScreen(
                                navController = navController,
                                viewModel = settingsViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}