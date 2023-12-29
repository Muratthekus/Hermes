package me.thekusch.hermes.core.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/**
 * OtpRequestWorker is a worker to prevent too many OTP request within a time period.
 * It will be started with the first OTP request and when the threshold time passed or
 * for the signup process, if the signup completed before the threshold time passed
 * it will clear the OTP request count.
 * */
internal class OtpRequestWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
): CoroutineWorker(context,workerParameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.Default) {
            try {
                ensureActive()
                val startTime = System.currentTimeMillis()
                while (System.currentTimeMillis() - startTime < SUSPENSION_THRESHOLD_MILLIS) {
                    delay(1000) // Check elapsed time every second
                }
                clearOtpRequestWithInThreshold()
                Result.success()
            } catch (e: Exception) {
                clearOtpRequestWithInThreshold()
                Result.failure()
            }
        }
    }

    private fun clearOtpRequestWithInThreshold() {
        getSharedPref().edit {
            putInt(otpRequestWithInThreshold, 0)
        }
    }

    private fun getSharedPref(): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Initialize/open an instance of EncryptedSharedPreferences on below line.
        return EncryptedSharedPreferences.create(
            // passing a file name to share a preferences
            context.packageName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        const val OTP_REQUEST_TAG = "OtpRequestManager"
        const val SUSPENSION_THRESHOLD_MILLIS = 180 * 1000L
        const val otpRequestWithInThreshold = "otpRequestWithInThreshold"

        @JvmStatic
        fun build() =
            OneTimeWorkRequestBuilder<OtpRequestWorker>()
                .addTag(OTP_REQUEST_TAG)
                .build()
    }
}