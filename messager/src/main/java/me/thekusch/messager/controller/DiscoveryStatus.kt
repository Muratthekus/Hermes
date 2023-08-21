package me.thekusch.messager.controller

public sealed interface DiscoveryStatus: BaseStatus {

    public object DiscoveryStarted: DiscoveryStatus

    public object DiscoveryFailed: DiscoveryStatus

    public data class EndpointFound(
        val endpointId: String,
        val endpointName: String
    ): DiscoveryStatus

    public data class EndpointLost(
        val endpointId: String
    ): DiscoveryStatus

}