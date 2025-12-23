package com.opennotes.ui.theme

import androidx.compose.ui.graphics.Color

val PureBlack = Color(0xFF000000)
val PureWhite = Color(0xFFFFFFFF)
val SoftWhite = Color(0xFFF5F5F5)

val RedOrange = Color(0xffffab91)
val RedPink = Color(0xfff48fb1)
val BabyBlue = Color(0xff81deea)
val Violet = Color(0xffcf94da)
val LightGreen = Color(0xffe7ed9b)

val RedOrangeDark = Color(0xFF5D2A1E)
val RedPinkDark   = Color(0xFF5A1F3D)
val BabyBlueDark  = Color(0xFF1C3C40)
val VioletDark    = Color(0xFF3E1F47)
val GreenDark     = Color(0xFF3A3F1B)

val LightNoteColors = listOf(
    SoftWhite,
    RedOrange,
    LightGreen,
    Violet,
    BabyBlue,
    RedPink
)

val DarkNoteColors = listOf(
    PureBlack,
    RedOrangeDark,
    GreenDark,
    VioletDark,
    BabyBlueDark,
    RedPinkDark
)

object NoteColorPalette{
    val Light=LightNoteColors
    val Dark= DarkNoteColors
}