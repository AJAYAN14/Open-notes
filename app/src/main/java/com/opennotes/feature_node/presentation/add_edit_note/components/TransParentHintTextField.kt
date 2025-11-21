package com.opennotes.feature_node.presentation.add_edit_note.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun TransParentHintTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    onFocusChange: (FocusState) -> Unit,
    focusRequester: FocusRequester
) {
    val interactionSource = remember { MutableInteractionSource() }

    BasicTextField(
        value = text,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = textStyle,
        interactionSource = interactionSource,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged(onFocusChange),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier

            ) {
                if (text.isEmpty()) {
                    Text(
                        text = hint,
                        style = textStyle,
                        color = Color.DarkGray,
                    )
                }

                innerTextField()
            }
        }
    )
}