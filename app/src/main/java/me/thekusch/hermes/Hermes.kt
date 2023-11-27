package me.thekusch.hermes

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import me.thekusch.hermes.core.datasource.local.HermesLocalDataSource
import javax.inject.Inject

@HiltAndroidApp
class Hermes: Application() {

    @Inject
    lateinit var hermesLocalDataSource: HermesLocalDataSource

    override fun onCreate() {
        super.onCreate()
        hermesLocalDataSource.init(applicationContext)
    }
}