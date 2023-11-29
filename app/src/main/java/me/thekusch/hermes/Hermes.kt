package me.thekusch.hermes

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Hermes: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}