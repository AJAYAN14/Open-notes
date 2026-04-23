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

package com.opennotes.ui.theme

import androidx.compose.ui.graphics.Color

val PureBlack = Color(0xFF000000)
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