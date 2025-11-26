package com.opennotes.feature_node.presentation.add_edit_note.components.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.opennotes.feature_node.presentation.settings.SettingsViewModel

@Composable
fun MarkdownCodeBlock(
    color: Color,
    text: @Composable (() -> Unit)
) {
    Box(
        modifier = Modifier.padding(top = 6.dp),
        content = {
            Surface(
                color = color,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .fillMaxWidth(),
                content = {
                    text()
                }
            )
        }
    )
}

@Composable
fun MarkdownQuote(content: String, fontSize: TextUnit) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .height(22.dp)
                .width(6.dp)
                .background(
                    Color(0xFF1565C0),
                    RoundedCornerShape(16.dp)
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = content,
            fontSize = fontSize,
            color = Color.Black,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
fun MarkdownCheck(content: @Composable () -> Unit, checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(24.dp),
            colors = CheckboxDefaults.colors(
                checkmarkColor = Color.White,
                checkedColor = Color(0xFF1565C0),
                uncheckedColor = Color.Black
            )
        )
        content()
    }
}

@Composable
fun MarkdownText(
    radius: Int,
    markdown: String,
    isPreview: Boolean = false,
    isEnabled: Boolean,
    modifier: Modifier = Modifier.fillMaxWidth(),
    weight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 16.sp,
    spacing: Dp = 2.dp,
    onContentChange: (String) -> Unit = {},
    settingsViewModel: SettingsViewModel? = null
) {
    if (!isEnabled || markdown.isBlank()) {
        StaticMarkdownText(
            markdown = if (markdown.isBlank()) "Start typing..." else markdown,
            modifier = modifier,
            weight = weight,
            fontSize = fontSize
        )
        return
    }

    // Safety check - use simple text rendering for potentially problematic content
    val hasComplexContent = remember(markdown) {
        markdown.length > 5000 ||
                markdown.count { it == '*' } > 50 ||
                markdown.count { it == '[' } > 20 ||
                (markdown.contains("http://") && markdown.lines().any { line ->
                    line.indexOf("http://", line.indexOf("http://") + 1) != -1
                }) ||
                (markdown.contains("https://") && markdown.lines().any { line ->
                    line.indexOf("https://", line.indexOf("https://") + 1) != -1
                })
    }

    if (hasComplexContent) {
        Text(
            text = markdown,
            fontSize = fontSize,
            fontWeight = weight,
            color = Color.Black,
            modifier = modifier
        )
        return
    }

    // Cache expensive markdown parsing
    val parsedContent = remember(markdown) {
        try {
            val lines = markdown.lines()
            val lineProcessors = listOf(
                HeadingProcessor(),
                ListItemProcessor(),
                CodeBlockProcessor(),
                QuoteProcessor(),
                ImageInsertionProcessor(),
                CheckboxProcessor(),
                LinkProcessor(),
                HorizontalRuleProcessor()
            )
            val markdownBuilder = MarkdownBuilder(lines, lineProcessors)
            markdownBuilder.parse()
            markdownBuilder.content
        } catch (e: Exception) {
            listOf(NormalText(markdown))
        }
    }

    MarkdownContent(
        radius = radius,
        isPreview = isPreview,
        content = parsedContent,
        modifier = modifier,
        spacing = spacing,
        weight = weight,
        fontSize = fontSize,
        lines = markdown.lines(),
        onContentChange = onContentChange
    )
}

@Composable
fun StaticMarkdownText(
    markdown: String,
    modifier: Modifier,
    weight: FontWeight,
    fontSize: TextUnit
) {
    Text(
        text = markdown,
        fontSize = fontSize,
        fontWeight = weight,
        color = if (markdown == "Start typing...")
            Color.Gray
        else
            Color.Black,
        modifier = modifier
    )
}

@Composable
fun MarkdownContent(
    radius: Int,
    isPreview: Boolean,
    content: List<MarkdownElement>,
    modifier: Modifier,
    spacing: Dp,
    weight: FontWeight,
    fontSize: TextUnit,
    lines: List<String>,
    onContentChange: (String) -> Unit
) {
    if (content.isEmpty()) {
        Text(
            text = "Start typing...",
            fontSize = fontSize,
            fontWeight = weight,
            color = Color.Gray,
            modifier = modifier
        )
        return
    }

    if (isPreview) {
        Column(modifier = modifier) {
            content.take(4).forEachIndexed { index, _ ->
                RenderMarkdownElement(
                    radius = radius,
                    index = index,
                    content = content,
                    weight = weight,
                    fontSize = fontSize,
                    lines = lines,
                    isPreview = true,
                    onContentChange = onContentChange
                )
                if (index < content.size - 1 && index < 3) {
                    Spacer(modifier = Modifier.height(spacing))
                }
            }
        }
    } else {
        SelectionContainer {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                items(content.size) { index ->
                    RenderMarkdownElement(
                        radius = radius,
                        content = content,
                        index = index,
                        weight = weight,
                        fontSize = fontSize,
                        lines = lines,
                        isPreview = isPreview,
                        onContentChange = onContentChange
                    )
                }
            }
        }
    }
}

@Composable
fun RenderMarkdownElement(
    radius: Int,
    content: List<MarkdownElement>,
    index: Int,
    weight: FontWeight,
    fontSize: TextUnit,
    lines: List<String>,
    isPreview: Boolean,
    onContentChange: (String) -> Unit
) {
    val element = content[index]
    val textColor = Color.Black

    when (element) {
        is Heading -> {
            Text(
                text = buildAnnotatedMarkdownString(element.text, weight),
                fontSize = when (element.level) {
                    1 -> (fontSize.value + 8).sp
                    2 -> (fontSize.value + 6).sp
                    3 -> (fontSize.value + 4).sp
                    4 -> (fontSize.value + 2).sp
                    5 -> (fontSize.value + 1).sp
                    6 -> fontSize
                    else -> fontSize
                },
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        is CheckboxItem -> {
            MarkdownCheck(
                content = {
                    Text(
                        text = buildAnnotatedMarkdownString(element.text, weight),
                        fontSize = fontSize,
                        fontWeight = weight,
                        color = textColor
                    )
                },
                checked = element.checked,
                onCheckedChange = if (isPreview) null else { newChecked ->
                    val newMarkdown = lines.toMutableList().apply {
                        this[element.index] = if (newChecked) {
                            "[X] ${element.text}"
                        } else {
                            "[ ] ${element.text}"
                        }
                    }
                    onContentChange(newMarkdown.joinToString("\n"))
                }
            )
        }

        is ListItem -> {
            val prefix = if (element.isNumbered && element.number != null) {
                "${element.number}. "
            } else {
                "• "
            }
            Text(
                text = buildAnnotatedMarkdownString("$prefix${element.text}", weight),
                fontSize = fontSize,
                fontWeight = weight,
                color = textColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        is Quote -> {
            MarkdownQuote(content = element.text, fontSize = fontSize)
        }

        is CodeBlock -> {
            if (element.isEnded) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE0E0E0))
                        .fillMaxWidth()
                ) {
                    Column {
                        element.language?.let { lang ->
                            Text(
                                text = lang.uppercase(),
                                fontSize = (fontSize.value - 2).sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF666666),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = element.code.trimEnd(),
                            fontSize = (fontSize.value - 1).sp,
                            fontWeight = weight,
                            fontFamily = FontFamily.Monospace,
                            color = textColor,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = buildAnnotatedMarkdownString(element.firstLine, weight),
                    fontWeight = weight,
                    fontSize = fontSize,
                    color = textColor
                )
            }
        }


        is Link -> {
            val context = LocalContext.current
            val linkColor = Color(0xFF0D47A1) // Dark blue for links

            val annotatedString = remember(element.fullText, element.urlRanges) {
                buildAnnotatedString {
                    try {
                        val fullText = element.fullText
                        var lastIndex = 0

                        val safeRanges = element.urlRanges
                            .take(3)
                            .filter { (url, range) ->
                                url.isNotEmpty() &&
                                        range.first >= 0 &&
                                        range.last < fullText.length &&
                                        range.first <= range.last
                            }
                            .sortedBy { it.second.first }

                        for ((url, range) in safeRanges) {
                            val safeStart = maxOf(0, minOf(range.first, fullText.length))
                            val safeEnd = maxOf(safeStart, minOf(range.last + 1, fullText.length))

                            // Add text before the link
                            if (safeStart > lastIndex && lastIndex < fullText.length) {
                                val textBefore = fullText.substring(lastIndex, safeStart)
                                // Process the text before for inline markdown
                                var i = 0
                                while (i < textBefore.length) {
                                    when {
                                        i < textBefore.length - 1 && textBefore[i] == '*' && textBefore[i + 1] == '*' -> {
                                            val end = textBefore.indexOf("**", i + 2)
                                            if (end != -1) {
                                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = textColor)) {
                                                    append(textBefore.substring(i + 2, end))
                                                }
                                                i = end + 2
                                            } else {
                                                withStyle(SpanStyle(color = textColor)) {
                                                    append(textBefore[i])
                                                }
                                                i++
                                            }
                                        }
                                        textBefore[i] == '*' -> {
                                            val end = textBefore.indexOf('*', i + 1)
                                            if (end != -1) {
                                                withStyle(SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = textColor)) {
                                                    append(textBefore.substring(i + 1, end))
                                                }
                                                i = end + 1
                                            } else {
                                                withStyle(SpanStyle(color = textColor)) {
                                                    append(textBefore[i])
                                                }
                                                i++
                                            }
                                        }
                                        else -> {
                                            withStyle(SpanStyle(color = textColor, fontWeight = weight)) {
                                                append(textBefore[i])
                                            }
                                            i++
                                        }
                                    }
                                }
                            }

                            // Add the link itself in BLUE
                            if (safeStart < safeEnd && url.length <= 200) {
                                pushStringAnnotation("URL", url)
                                withStyle(SpanStyle(
                                    color = linkColor,  // BLUE color for the link
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                                )) {
                                    append(url)
                                }
                                pop()
                            }

                            lastIndex = safeEnd
                        }

                        // Add remaining text after all links
                        if (lastIndex < fullText.length) {
                            val textAfter = fullText.substring(lastIndex)
                            var i = 0
                            while (i < textAfter.length) {
                                when {
                                    i < textAfter.length - 1 && textAfter[i] == '*' && textAfter[i + 1] == '*' -> {
                                        val end = textAfter.indexOf("**", i + 2)
                                        if (end != -1) {
                                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = textColor)) {
                                                append(textAfter.substring(i + 2, end))
                                            }
                                            i = end + 2
                                        } else {
                                            withStyle(SpanStyle(color = textColor)) {
                                                append(textAfter[i])
                                            }
                                            i++
                                        }
                                    }
                                    textAfter[i] == '*' -> {
                                        val end = textAfter.indexOf('*', i + 1)
                                        if (end != -1) {
                                            withStyle(SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = textColor)) {
                                                append(textAfter.substring(i + 1, end))
                                            }
                                            i = end + 1
                                        } else {
                                            withStyle(SpanStyle(color = textColor)) {
                                                append(textAfter[i])
                                            }
                                            i++
                                        }
                                    }
                                    else -> {
                                        withStyle(SpanStyle(color = textColor, fontWeight = weight)) {
                                            append(textAfter[i])
                                        }
                                        i++
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withStyle(SpanStyle(
                            fontWeight = weight,
                            color = textColor
                        )) {
                            append(element.fullText)
                        }
                    }
                }
            }

            ClickableText(
                text = annotatedString,
                onClick = { offset: Int ->
                    try {
                        annotatedString.getStringAnnotations("URL", offset, offset)
                            .firstOrNull()?.let { annotation ->
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                                intent.data = android.net.Uri.parse(annotation.item)
                                context.startActivity(intent)
                            }
                    } catch (e: Exception) {
                        // Handle error silently
                    }
                },
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = fontSize
                )
            )
        }

        is HorizontalRule -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(1.dp)
                    .background(Color(0xFF666666))
            )
        }

        is NormalText -> {
            Text(
                text = buildAnnotatedMarkdownString(element.text, weight),
                fontSize = fontSize,
                fontWeight = weight,
                color = textColor
            )
        }

        else -> {
            // Handle any other types
        }
    }
}