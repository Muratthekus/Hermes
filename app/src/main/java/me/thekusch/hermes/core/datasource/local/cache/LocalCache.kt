package me.thekusch.hermes.core.datasource.local.cache

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class LocalCache @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private val Keys = object {
        val otpRequestWithInThreshold = "otpRequestWithInThreshold"
        val otpRequestThreshold = 4
    }

    fun getOtpRequestWithInThreshold(): Int {
        return sharedPreferences.getInt(Keys.otpRequestWithInThreshold, 0)
    }

    fun increaseOtpRequestWithInThreshold() {
        val latestCount = getOtpRequestWithInThreshold() + 1
        sharedPreferences.edit {
            putInt(Keys.otpRequestWithInThreshold, latestCount)
        }
    }

    fun isOtpRequestReachedThreshold(): Boolean {
        return getOtpRequestWithInThreshold() >= Keys.otpRequestThreshold
    }

    fun clearOtpRequestWithInThreshold() {
        sharedPreferences.edit {
            putInt(Keys.otpRequestWithInThreshold, 0)
        }
    }

}