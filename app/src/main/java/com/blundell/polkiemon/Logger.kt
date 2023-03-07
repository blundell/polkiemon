package com.blundell.polkiemon

import android.util.Log

/**
 * In a production application, logging would be a lot more intricate and detailed.
 * Correct Log tags and more automated logging to help pinpoint log location.
 * Logging at different levels, verbose, error etc.
 * Logging different things in different environments.
 * Logging with breadcrumbs and capturing exception stack traces and other details.
 */
interface Logger {
    fun d(msg: String)
}

object AndroidLogger : Logger {
    override fun d(msg: String) {
        Log.d("PKM", msg)
    }
}

object VoidLogger : Logger {
    override fun d(msg: String) {
        // Into the void it goes
        // In a production app, you may have more granular control over what is logged and when
    }
}
