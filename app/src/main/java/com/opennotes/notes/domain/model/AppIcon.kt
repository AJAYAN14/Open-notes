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

package com.opennotes.notes.domain.model

enum class AppIcon(
    val componentClass: String,
    val title: String,
    val colorHex: Long,
) {
    DEFAULT("com.opennotes.featureNode.presentation.MainActivityDefault", "Default (Orange)", 0xFFFF9800),
    RED("com.opennotes.featureNode.presentation.MainActivityRed", "Red", 0xFFF44336),
    PINK("com.opennotes.featureNode.presentation.MainActivityPink", "Pink", 0xFFE91E63),
    PURPLE("com.opennotes.featureNode.presentation.MainActivityPurple", "Purple", 0xFF9C27B0),
    DEEP_PURPLE("com.opennotes.featureNode.presentation.MainActivityDeepPurple", "Deep Purple", 0xFF673AB7),
    INDIGO("com.opennotes.featureNode.presentation.MainActivityIndigo", "Indigo", 0xFF3F51B5),
    BLUE("com.opennotes.featureNode.presentation.MainActivityBlue", "Blue", 0xFF2196F3),
    LIGHT_BLUE("com.opennotes.featureNode.presentation.MainActivityLightBlue", "Light Blue", 0xFF03A9F4),
    CYAN("com.opennotes.featureNode.presentation.MainActivityCyan", "Cyan", 0xFF00BCD4),
    TEAL("com.opennotes.featureNode.presentation.MainActivityTeal", "Teal", 0xFF009688),
    GREEN("com.opennotes.featureNode.presentation.MainActivityGreen", "Green", 0xFF4CAF50),
    LIGHT_GREEN("com.opennotes.featureNode.presentation.MainActivityLightGreen", "Light Green", 0xFF8BC34A),
    LIME("com.opennotes.featureNode.presentation.MainActivityLime", "Lime", 0xFFCDDC39),
    YELLOW("com.opennotes.featureNode.presentation.MainActivityYellow", "Yellow", 0xFFFFEB3B),
    AMBER("com.opennotes.featureNode.presentation.MainActivityAmber", "Amber", 0xFFFFC107),

    DEEP_ORANGE("com.opennotes.featureNode.presentation.MainActivityDeepOrange", "Deep Orange", 0xFFFF5722),
    BROWN("com.opennotes.featureNode.presentation.MainActivityBrown", "Brown", 0xFF795548),
    GREY("com.opennotes.featureNode.presentation.MainActivityGrey", "Grey", 0xFF9E9E9E),
    BLUE_GREY("com.opennotes.featureNode.presentation.MainActivityBlueGrey", "Blue Grey", 0xFF607D8B),
    BLACK("com.opennotes.featureNode.presentation.MainActivityBlack", "Black", 0xFF000000),
}
