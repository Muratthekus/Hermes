package me.thekusch.hermes.core.datasource.local.cache

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HermesLocalDataSource @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private val NAME = "NAME"

    internal var name: String?
        get() {
            return sharedPreferences.getString(NAME, null)
        }
        set(value) {
            sharedPreferences.edit {
                putString(NAME, value)
            }
        }

}