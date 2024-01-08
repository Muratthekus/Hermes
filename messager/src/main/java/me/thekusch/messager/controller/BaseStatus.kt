package me.thekusch.messager.controller

import java.lang.Exception

public sealed interface BaseStatus {

    public interface WavingStarting : BaseStatus

    public interface WavingMatchDetecting : BaseStatus

    public object Initial : BaseStatus

    public object Loading : WavingStarting

    public object StartFinishedWithSuccess : WavingStarting

    public data class StartFinishedWithError(
        val exception: Exception
    ) : WavingStarting

    public data class EndpointFound(
        val endpointId: String,
        val endpointName: String
    ) : WavingMatchDetecting

    public data class EndpointLost(
        val endpointId: String
    ) : WavingMatchDetecting

    public data class ConnectionInitiated(
        val endpointId: String,
        val endpointName: String
    ) : WavingMatchDetecting

    public data class ConnectionResultStatus(val result: String) : WavingMatchDetecting {
        public companion object {
            public const val CONNECTED: String = "CONNECTED"
            public const val ERROR: String = "ERROR"
            public const val REJECTED: String = "REJECTED"
        }
    }

    public object Talking : BaseStatus

    public object Disconnected : BaseStatus

    public object Dismissed : BaseStatus

}