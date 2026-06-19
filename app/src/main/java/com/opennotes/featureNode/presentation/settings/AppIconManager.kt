package com.opennotes.featureNode.presentation.settings

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.opennotes.featureNode.domain.model.AppIcon

object AppIconManager {
    fun setAppIcon(
        context: Context,
        activeIcon: AppIcon,
    ) {
        val packageManager = context.packageManager

        AppIcon.values().forEach { icon ->
            val state =
                if (icon == activeIcon) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }

            packageManager.setComponentEnabledSetting(
                ComponentName(context, icon.componentClass),
                state,
                PackageManager.DONT_KILL_APP,
            )
        }
    }
}
