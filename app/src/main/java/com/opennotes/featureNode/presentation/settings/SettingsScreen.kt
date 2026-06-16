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

package com.opennotes.featureNode.presentation.settings

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.opennotes.featureNode.presentation.util.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SettingsSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        thumbContent = {
            Icon(
                imageVector = if (isChecked) Icons.Default.Check else Icons.Default.Close,
                contentDescription = if (isChecked) "On" else "Off",
                modifier = Modifier.size(18.dp),
                tint =
                    if (isChecked) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
            )
        },
        colors =
            SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val versionName =
        remember {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "Unknown"
        }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is SettingsViewModel.UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = event.message)
                    }
                }

                SettingsViewModel.UiEvent.RequestBiometricAuthForEnable -> {
                    if (activity == null) {
                        viewModel.onBiometricAuthFailed()
                        return@collectLatest
                    }

                    val biometricManager = BiometricManager.from(context)
                    val canAuthenticate =
                        biometricManager.canAuthenticate(
                            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL,
                        )

                    if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Biometric authentication is not available")
                        }
                        viewModel.onBiometricAuthFailed()
                        return@collectLatest
                    }

                    val executor = ContextCompat.getMainExecutor(context)
                    val prompt =
                        BiometricPrompt(
                            activity,
                            executor,
                            object : BiometricPrompt.AuthenticationCallback() {
                                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                    super.onAuthenticationSucceeded(result)
                                    viewModel.onBiometricAuthSuccess()
                                }

                                override fun onAuthenticationError(
                                    errorCode: Int,
                                    errString: CharSequence,
                                ) {
                                    super.onAuthenticationError(errorCode, errString)
                                    viewModel.onBiometricAuthFailed()
                                }
                            },
                        )

                    val promptInfo =
                        BiometricPrompt.PromptInfo
                            .Builder()
                            .setTitle("Enable biometric lock")
                            .setSubtitle("Confirm your identity to enable app lock")
                            .setAllowedAuthenticators(
                                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                                    BiometricManager.Authenticators.DEVICE_CREDENTIAL,
                            ).build()

                    prompt.authenticate(promptInfo)
                }

                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style =
                            MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.15.sp,
                            ),
                    )
                },
                colors =
                    TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            item {
                SettingItem(
                    title = "Backup and Restore",
                    subtitle = "Export and import your notes",
                    icon = Icons.Default.Cloud,
                    onClick = { navController.navigate(Screen.BackupScreen.route) },
                    isFirst = true,
                    isLast = true,
                )
            }

            item {
                SettingItem(
                    title = "Appearance",
                    subtitle = "Theme, Color scheme, Black Theme",
                    icon = Icons.Default.Palette,
                    onClick = { navController.navigate(Screen.AppearanceSettingsScreen.route) },
                    isFirst = true,
                    isLast = true,
                )
            }

            item {
                SettingItem(
                    title = "Privacy",
                    subtitle = "Biometric lock",
                    icon = Icons.Default.Fingerprint,
                    onClick = { navController.navigate(Screen.PrivacySettingsScreen.route) },
                    isFirst = true,
                    isLast = true,
                )
            }

            item {
                SettingItem(
                    title = "About",
                    subtitle = "Version $versionName",
                    icon = Icons.Default.Info,
                    onClick = {
                        navController.navigate(Screen.AboutScreen.route)
                    },
                    isFirst = true,
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
