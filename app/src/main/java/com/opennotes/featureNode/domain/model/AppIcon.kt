package com.opennotes.featureNode.domain.model

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
    ORANGE("com.opennotes.featureNode.presentation.MainActivityOrange", "Orange", 0xFFFF9800),
    DEEP_ORANGE("com.opennotes.featureNode.presentation.MainActivityDeepOrange", "Deep Orange", 0xFFFF5722),
    BROWN("com.opennotes.featureNode.presentation.MainActivityBrown", "Brown", 0xFF795548),
    GREY("com.opennotes.featureNode.presentation.MainActivityGrey", "Grey", 0xFF9E9E9E),
    BLUE_GREY("com.opennotes.featureNode.presentation.MainActivityBlueGrey", "Blue Grey", 0xFF607D8B),
    BLACK("com.opennotes.featureNode.presentation.MainActivityBlack", "Black", 0xFF000000),
}
