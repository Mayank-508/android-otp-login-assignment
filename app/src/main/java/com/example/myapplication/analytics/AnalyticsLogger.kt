package com.example.myapplication.analytics

import timber.log.Timber

object AnalyticsLogger {
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        Timber.d("Event: $eventName, Params: $params")
    }

    fun logError(message: String, throwable: Throwable? = null) {
        Timber.e(throwable, message)
    }
}
