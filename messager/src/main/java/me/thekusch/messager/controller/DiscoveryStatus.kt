package me.thekusch.messager.controller

import java.lang.Exception

public sealed interface DiscoveryStatus: BaseStatus {

    public object Loading: DiscoveryStatus

    public data class ConnectionInitiated(
        val endpointId: String,
        val endpointName: String
    ): DiscoveryStatus

    public data class ConnectionResultStatus(val result: String): DiscoveryStatus{
        public companion object {
            public const val CONNECTED = "CONNECTED"
            public const val ERROR = "ERROR"
            public const val REJECTED =  "REJECTED"
        }
    }

    public object Disconnected: DiscoveryStatus

    public object DiscoveryStarted: DiscoveryStatus

    public data class DiscoveryFailed(
        val exception: Exception
    ): DiscoveryStatus

    public data class EndpointFound(
        val endpointId: String,
        val endpointName: String
    ): DiscoveryStatus

    public data class EndpointLost(
        val endpointId: String
    ): DiscoveryStatus

}