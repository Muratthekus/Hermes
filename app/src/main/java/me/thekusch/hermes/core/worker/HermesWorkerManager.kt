package me.thekusch.hermes.core.worker

import android.content.Context
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import me.thekusch.hermes.core.datasource.local.cache.LocalCache
import me.thekusch.hermes.core.worker.OtpRequestWorker.Companion.OTP_REQUEST_TAG
import javax.inject.Inject

class HermesWorkerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localCache: LocalCache
) {

    private val workManager: WorkManager by lazy { WorkManager.getInstance(context) }

    fun safeStartOtpRequestWorker() {
        /** with the first OTP request, OtpRequestWorker will be started.
         * If the request count is more then 0, no need to start it again
         */
        if (localCache.getOtpRequestWithInThreshold() > 0)
            return
        workManager.enqueue(OtpRequestWorker.build())
    }

    fun cancelOtpRequestWorker() {
        if (localCache.getOtpRequestWithInThreshold() > 0)
            return
        workManager.cancelAllWorkByTag(OTP_REQUEST_TAG)
    }

}