package me.thekusch.hermes

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import me.thekusch.hermes.core.datasource.local.HermesLocalDataSource

@HiltAndroidApp
class Hermes: Application() {

    override fun onCreate() {
        super.onCreate()
        HermesLocalDataSource.init(applicationContext)
    }
}