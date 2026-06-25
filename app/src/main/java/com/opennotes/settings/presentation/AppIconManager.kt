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

package com.opennotes.settings.presentation

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.opennotes.notes.domain.model.AppIcon

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
