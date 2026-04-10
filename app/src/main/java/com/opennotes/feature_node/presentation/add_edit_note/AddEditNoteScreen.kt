package com.opennotes.feature_node.presentation.add_edit_note


import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.opennotes.feature_node.presentation.add_edit_note.components.TransParentHintTextField
import com.opennotes.feature_node.presentation.add_edit_note.components.markdown.MarkdownText
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .imePadding()
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

            TransParentHintTextField(
                text = titleState.text,
                hint = titleState.hint,
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it)) },
                onFocusChange = { viewModel.onEvent(AddEditNoteEvent.changeTitleFocus(it)) },
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(color = contentColor),
                focusRequester = titleFocusRequester,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (isPreviewMode) {
                    MarkdownText(
                        radius = 8,
                        markdown = contentState.text.ifBlank { "No content to preview" },
                        isPreview = false,
                        isEnabled = true,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        onContentChange = {},
                        settingsViewModel = null,
                        textColor = contentColor
                    )
                } else {
                    val scrollState = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                contentFocusRequester.requestFocus()
                            }
                    ) {
                        TransParentHintTextField(
                            text = contentState.text,
                            hint = contentState.hint,
                            onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredContent(it)) },
                            onFocusChange = { viewModel.onEvent(AddEditNoteEvent.changeContentFocus(it)) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = contentColor),
                            singleLine = false,
                            focusRequester = contentFocusRequester,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(unbounded = true)
                        )
                    }
                }
            }
        }
    }
}