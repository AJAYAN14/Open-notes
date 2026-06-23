package com.opennotes.util

import com.opennotes.BuildConfig

/**
 * Global configuration constants for the app.
 * Abstracts away build flavors and environment variables from the UI layers.
 */
object AppConfig {
    /**
     * Determines whether donation/sponsor links should be visible.
     * Google Play Developer Policy strictly forbids external donation links.
     */
    val showDonations: Boolean = BuildConfig.FLAVOR != "playStore"
}
