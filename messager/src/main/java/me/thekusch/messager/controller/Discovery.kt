package me.thekusch.messager.controller

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import me.thekusch.messager.DiscoveryStatusListener
import me.thekusch.messager.datasource.LocalDataSource


internal class Discovery {

    private var strategy = Strategy.P2P_POINT_TO_POINT
    lateinit var listener: DiscoveryStatusListener
    private val localDataSource = LocalDataSource

    internal fun disconnect(context: Context) {
        listener.invoke(BaseStatus.Disconnected)
        stopDiscovery(context)
    }

    internal fun dismiss(context: Context) {
        listener.invoke(BaseStatus.Dismissed)
        stopDiscovery(context)
    }

    private fun stopDiscovery(context: Context) {
        Nearby.getConnectionsClient(context)
            .stopDiscovery()
    }


    internal fun acceptConnectionRequest(
        context: Context,
        endpointId: String
    ) {

        Nearby.getConnectionsClient(context)
            .acceptConnection(endpointId, getPayloadCallBack())
    }

    internal fun rejectConnectionRequest(
        context: Context,
        endpointId: String
    ) {
        Nearby.getConnectionsClient(context)
            .rejectConnection(endpointId)
    }

    internal fun startDiscovery(
        context: Context
    ) {
        listener.invoke(BaseStatus.Loading)

        val connectionsClient = Nearby.getConnectionsClient(context)
        connectionsClient.stopAdvertising()
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(strategy).build()
        connectionsClient
            .startDiscovery(
                context.packageName,
                getEndpointDiscoveryCallback(context),
                discoveryOptions
            )
            .addOnSuccessListener {
                listener.invoke(
                    BaseStatus.StartFinishedWithSuccess
                )
            }
            .addOnFailureListener {
                listener.invoke(
                    BaseStatus.StartFinishedWithError(it)
                )
            }
    }

    private fun getEndpointDiscoveryCallback(context: Context): EndpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                // An endpoint was found. We request a connection to it.
                listener.invoke(
                    BaseStatus.EndpointFound(
                        endpointId, info.endpointName
                    )
                )
                Nearby.getConnectionsClient(context)
                    .requestConnection(
                        localDataSource.username ?: "",
                        endpointId,
                        getConnectionLifecycleCallback(context)
                    )
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { e: java.lang.Exception? -> }
            }

            override fun onEndpointLost(endpointId: String) {
                listener.invoke(
                    BaseStatus.EndpointLost(
                        endpointId
                    )
                )
            }
        }

    private fun getConnectionLifecycleCallback(context: Context): ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                // Automatically accept the connection on both sides.
                listener.invoke(
                    BaseStatus.ConnectionInitiated(
                        endpointId, connectionInfo.endpointName
                    )
                )
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        listener.invoke(
                            BaseStatus.ConnectionResultStatus(
                                BaseStatus.ConnectionResultStatus.CONNECTED
                            )
                        )
                    }

                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        listener.invoke(
                            BaseStatus.ConnectionResultStatus(
                                BaseStatus.ConnectionResultStatus.REJECTED
                            )
                        )
                    }

                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        listener.invoke(
                            BaseStatus.ConnectionResultStatus(
                                BaseStatus.ConnectionResultStatus.ERROR
                            )
                        )
                    }

                    else -> {}
                }
            }

            override fun onDisconnected(endpointId: String) {
                listener.invoke(
                    BaseStatus.Disconnected
                )
            }
        }

    private fun getPayloadCallBack(): PayloadCallback =
        object : PayloadCallback() {
            override fun onPayloadReceived(p0: String, p1: Payload) {
                TODO("Not yet implemented")
            }

            override fun onPayloadTransferUpdate(p0: String, p1: PayloadTransferUpdate) {
                TODO("Not yet implemented")
            }

        }
}