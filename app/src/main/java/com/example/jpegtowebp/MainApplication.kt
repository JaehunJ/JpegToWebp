package com.example.jpegtowebp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}