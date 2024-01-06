package me.thekusch.messager.controller

import java.lang.Exception

public sealed interface DiscoveryStatus : BaseStatus {

    public object Loading : DiscoveryStatus, BaseStatus.WavingStarting

    public object StartFinishedWithSuccess : DiscoveryStatus, BaseStatus.WavingStarting

    public data class StartFinishedWithError(
        val exception: Exception
    ) : DiscoveryStatus, BaseStatus.WavingStarting

    public data class EndpointFound(
        val endpointId: String,
        val endpointName: String
    ) : DiscoveryStatus, BaseStatus.WavingMatchDetecting

    public data class EndpointLost(
        val endpointId: String
    ) : DiscoveryStatus, BaseStatus.WavingMatchDetecting

    public data class ConnectionInitiated(
        val endpointId: String,
        val endpointName: String
    ) : DiscoveryStatus, BaseStatus.WavingMatchDetecting

    public data class ConnectionResultStatus(val result: String) : DiscoveryStatus,
        BaseStatus.WavingMatchDetecting {
        public companion object {
            public const val CONNECTED: String = "CONNECTED"
            public const val ERROR: String = "ERROR"
            public const val REJECTED: String = "REJECTED"
        }
    }

}