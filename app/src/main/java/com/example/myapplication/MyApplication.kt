package com.example.myapplication

import android.app.Application
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (com.example.myapplication.BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
