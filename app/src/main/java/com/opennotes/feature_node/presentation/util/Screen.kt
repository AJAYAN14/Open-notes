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

package com.opennotes.feature_node.presentation.util

sealed class Screen(val route:String){
    object NotesScreen: Screen("notes_screen")
    object AddEditNoteScreen: Screen("add_edit_note_screen")
    object SettingsScreen:Screen ("settings_screen")

    object AboutScreen : Screen("about_screen")

    object AppearanceScreen : Screen("appearance_screen")

    object PrivacyScreen : Screen("privacy_screen")

    object BackupScreen : Screen("backup_screen")

}