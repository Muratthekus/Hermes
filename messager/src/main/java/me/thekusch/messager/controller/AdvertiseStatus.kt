package me.thekusch.messager.controller

import java.lang.Exception

public sealed interface AdvertiseStatus: BaseStatus {

    public object Loading: AdvertiseStatus

    public data class ConnectionInitiated(
        val endpointId: String,
        val endpointName: String
    ): AdvertiseStatus

    public data class ConnectionResultStatus(val result: String): AdvertiseStatus{
        public companion object {
            public const val CONNECTED = "CONNECTED"
            public const val ERROR = "ERROR"
            public const val REJECTED =  "REJECTED"
        }
    }

    public object Disconnected: AdvertiseStatus

    public object FinishedSuccessfully: AdvertiseStatus

    public data class FinishedWithError(
        val exception: Exception
    ): AdvertiseStatus
}