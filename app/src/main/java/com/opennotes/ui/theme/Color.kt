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

val DefaultDark = Color(0xFF000000)
val DefaultLight = Color(0xFFF5F5F5)

val RedLight = Color(0xFFF28B82)
val OrangeLight = Color(0xFFFBBC04)
val YellowLight = Color(0xFFFFF475)
val GreenLight = Color(0xFFCCFF90)
val TealLight = Color(0xFFA7FFEB)
val BlueLight = Color(0xFFCBF0F8)
val DarkBlueLight = Color(0xFFAECBFA)
val PurpleLight = Color(0xFFD7AEFB)
val PinkLight = Color(0xFFFDCFE8)
val BrownLight = Color(0xFFE6C9A8)
val GrayLight = Color(0xFFE8EAED)

val RedDark = Color(0xFF5C2B29)
val OrangeDark = Color(0xFF614A19)
val YellowDark = Color(0xFF635D19)
val GreenDark = Color(0xFF345920)
val TealDark = Color(0xFF16504B)
val BlueDark = Color(0xFF2D555E)
val DarkBlueDark = Color(0xFF1E3A5F)
val PurpleDark = Color(0xFF42275E)
val PinkDark = Color(0xFF5B2245)
val BrownDark = Color(0xFF442F19)
val GrayDark = Color(0xFF3C3F41)

val LightNoteColors =
    listOf(
        DefaultLight,
        RedLight,
        OrangeLight,
        YellowLight,
        GreenLight,
        TealLight,
        BlueLight,
        DarkBlueLight,
        PurpleLight,
        PinkLight,
        BrownLight,
        GrayLight,
    )

val DarkNoteColors =
    listOf(
        DefaultDark,
        RedDark,
        OrangeDark,
        YellowDark,
        GreenDark,
        TealDark,
        BlueDark,
        DarkBlueDark,
        PurpleDark,
        PinkDark,
        BrownDark,
        GrayDark,
    )

object NoteColorPalette {
    val Light = LightNoteColors
    val Dark = DarkNoteColors
}
