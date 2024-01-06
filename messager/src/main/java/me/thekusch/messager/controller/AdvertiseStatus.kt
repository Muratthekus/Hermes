package me.thekusch.messager.controller

import java.lang.Exception

public sealed interface AdvertiseStatus : BaseStatus {

    public object Loading : AdvertiseStatus, BaseStatus.WavingStarting

    public object StartFinishedWithSuccess : AdvertiseStatus, BaseStatus.WavingStarting

    public data class StartFinishedWithError(
        val exception: Exception
    ) : AdvertiseStatus, BaseStatus.WavingStarting

    public data class ConnectionInitiated(
        val endpointId: String,
        val endpointName: String
    ) : AdvertiseStatus, BaseStatus.WavingMatchDetecting

    public data class ConnectionResultStatus(val result: String) : AdvertiseStatus,
        BaseStatus.WavingMatchDetecting {
        public companion object {
            public const val CONNECTED: String = "CONNECTED"
            public const val ERROR: String = "ERROR"
            public const val REJECTED: String = "REJECTED"
        }
    }

}