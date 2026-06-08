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

package com.opennotes.feature_node.presentation.add_edit_note


import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.opennotes.feature_node.presentation.add_edit_note.components.markdown.MarkdownField
import com.opennotes.ui.theme.NoteColorPalette
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteColor: Int?,
    isDarkTheme: Boolean,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value
    val snackbarHostState = remember { SnackbarHostState() }

    var isPreviewMode by remember { mutableStateOf(false) }

    val resolvedColorInt = remember(noteColor, viewModel.noteColor.value) {
        noteColor ?: viewModel.noteColor.value
    }

    val noteBackgroundAnimatable = remember { Animatable(Color(resolvedColorInt)) }


    LaunchedEffect(resolvedColorInt) {
        noteBackgroundAnimatable.animateTo(Color(resolvedColorInt))
    }

    val backgroundColor = noteBackgroundAnimatable.value

    val contentColor = if (backgroundColor.luminance() < 0.5f) {
        Color.White
    } else {
        Color.Black
    }

    val noteColors = if (isDarkTheme) {
        NoteColorPalette.Dark
    } else {
        NoteColorPalette.Light
    }

    val contentFocusRequester = remember { FocusRequester() }
    val titleFocusRequester = remember { FocusRequester() }


    val interactionSource = remember { MutableInteractionSource() }
    var showColorPicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = event.message)
                }

                is AddEditNoteViewModel.UiEvent.SavedNote -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = contentColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isPreviewMode = !isPreviewMode }) {
                        Icon(
                            imageVector = if (isPreviewMode) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isPreviewMode) "Edit mode" else "Preview mode",
                            tint = contentColor
                        )
                    }
                    IconButton(onClick = { viewModel.onEvent(AddEditNoteEvent.SaveNote) }) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save Note",
                            tint = contentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { paddingValues ->
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextSelectionColors provides TextSelectionColors(
                handleColor = contentColor,
                backgroundColor = contentColor.copy(alpha = 0.4f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {


                // Color picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    noteColors.forEach { color ->
                        val colorInt = remember(color) { color.toArgb() }
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(15.dp, CircleShape)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = 3.dp,
                                    color = if (viewModel.noteColor.value == colorInt) Color.Black else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    scope.launch {
                                        noteBackgroundAnimatable.animateTo(
                                            targetValue = Color(colorInt),
                                            animationSpec = tween(durationMillis = 500)
                                        )
                                    }
                                    viewModel.onEvent(AddEditNoteEvent.changeColor(colorInt))
                                }
                        )
                    }
                }



                Spacer(modifier = Modifier.height(16.dp))

                MarkdownField(
                    titleText = titleState.text,
                    contentText = contentState.text,
                    contentColor = contentColor,
                    isPreviewMode = isPreviewMode,
                    interactionSource = interactionSource,
                    contentFocusRequester = contentFocusRequester,
                    titleFocusRequester = titleFocusRequester,
                    onTitleChange = { viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it)) },
                    onTitleFocusChange = { viewModel.onEvent(AddEditNoteEvent.changeTitleFocus(it)) },
                    onContentChange = { viewModel.onEvent(AddEditNoteEvent.EnteredContent(it)) },
                    onContentFocusChange = {
                        viewModel.onEvent(
                            AddEditNoteEvent.changeContentFocus(
                                it
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .imePadding()
                )
            }
        }
    }
}