/*
 *
 *  *  Copyright (c) 2026 Dhanush Sugganahalli <dhanush41230@gmail.com>
 *  *
 *  *  This program is free software; you can redistribute it and/or modify it under
 *  *  the terms of the GNU General Public License as published by the Free Software
 *  *  Foundation; either version 3 of the License, or (at your option) any later
 *  *  version.
 *  *
 *  *  This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  *  PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *  *
 *  *  You should have received a copy of the GNU General Public License along with
 *  *  this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.opennotes.feature_node.presentation.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.opennotes.feature_node.presentation.util.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                viewModel.onImportClick(uri)
            }
        }
    )
    val exportFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.onExportUriSelected(it) }
        }
    )

    val settings by viewModel.settings.collectAsState()
    val showThemePicker = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SettingsViewModel.UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = event.message)
                    }
                }
                is SettingsViewModel.UiEvent.OpenExportPicker -> {
                    exportFileLauncher.launch(event.suggestedFileName)
                }
                SettingsViewModel.UiEvent.RequestBiometricAuthForEnable -> {
                    if (activity == null) {
                        viewModel.onBiometricAuthFailed()
                        return@collectLatest
                    }

                    val biometricManager = BiometricManager.from(context)
                    val canAuthenticate = biometricManager.canAuthenticate(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                BiometricManager.Authenticators.BIOMETRIC_WEAK
                    )

                    if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Biometric authentication is not available")
                        }
                        viewModel.onBiometricAuthFailed()
                        return@collectLatest
                    }

                    val executor = ContextCompat.getMainExecutor(context)
                    val prompt = BiometricPrompt(
                        activity,
                        executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(
                                result: BiometricPrompt.AuthenticationResult
                            ) {
                                super.onAuthenticationSucceeded(result)
                                viewModel.onBiometricAuthSuccess()
                            }

                            override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence
                            ) {
                                super.onAuthenticationError(errorCode, errString)
                                viewModel.onBiometricAuthFailed()
                            }

                        }
                    )

                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Enable biometric lock")
                        .setSubtitle("Confirm your fingerprint to enable app lock")
                        .setNegativeButtonText("Cancel")
                        .build()

                    prompt.authenticate(promptInfo)
                }

            }
        }
    }

    if (showThemePicker.value) {
        ThemePicker(
            currentTheme = settings.themeMode,
            onThemeSelected = { theme ->
                viewModel.updateThemeMode(theme)
            },
            onDismiss = { showThemePicker.value = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            item {
                Text(
                    text = "Backup & Restore",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }

            item {
                SettingItem(
                    title = "Export Notes",
                    subtitle = "Save your notes to a file",
                    icon = Icons.Default.CloudUpload,
                    onClick = { viewModel.onExportClick() },
                    isFirst = true,
                    trailing = {}
                )
            }

            item {
                SettingItem(
                    title = "Import Notes",
                    subtitle = "Load notes from a file",
                    icon = Icons.Default.CloudDownload,
                    onClick = {
                        filePickerLauncher.launch(arrayOf("application/json"))
                    },
                    isLast = true,
                    trailing = {}
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }
            item {
                SettingItem(
                    title = "Theme",
                    subtitle = when (settings.themeMode) {
                        ThemeMode.SYSTEM -> "System default"
                        ThemeMode.LIGHT -> "Light"
                        ThemeMode.DARK -> "Dark"
                    },
                    icon = Icons.Default.Palette,
                    onClick = { showThemePicker.value = true },
                    isFirst = true
                )
            }

            item {
                SettingItem(
                    title = "Black Theme",
                    subtitle = if (settings.blackTheme) "Use a pure black dark theme" else "Use regular dark colors",
                    icon = Icons.Default.DarkMode,
                    trailing = {
                        Switch(
                            checked = settings.blackTheme,
                            onCheckedChange = { isChecked ->
                                viewModel.updateBlackTheme(isChecked)
                            }
                        )
                    },
                    isLast = false
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }


            item {
                Text(
                    text = "Privacy",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }

            item {
                SettingItem(
                    title = "Biometric Lock",
                    subtitle = if (settings.biometricLock) "Fingerprint required when enabled" else "Require fingerprint to unlock app",
                    icon = Icons.Default.Fingerprint,
                    trailing = {
                        Switch(
                            checked = settings.biometricLock,
                            onCheckedChange = { enabled ->
                                viewModel.onBiometricLockToggleRequest(enabled)
                            }
                        )
                    },
                    isFirst = true,
                    isLast = true
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }



            item {
                SettingItem(
                    title = "About ",
                    subtitle="",
                    icon= Icons.Default.Info,
                    onClick = { navController.navigate(Screen.AboutScreen.route)
                    },
                    isFirst = true
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
