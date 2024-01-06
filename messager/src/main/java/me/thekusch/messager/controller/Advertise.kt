package me.thekusch.messager.controller

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import me.thekusch.messager.AdvertiseStatusListener
import me.thekusch.messager.datasource.LocalDataSource


internal class Advertise {

    private var strategy = Strategy.P2P_CLUSTER
    lateinit var listener: AdvertiseStatusListener
    private val localDataSource = LocalDataSource

    internal fun disconnect(context: Context) {
        listener.invoke(BaseStatus.Disconnected)
        stopAdvertising(context)
    }

    internal fun dismiss(context: Context) {
        listener.invoke(BaseStatus.Dismissed)
        stopAdvertising(context)
    }

    private fun stopAdvertising(
        context: Context,
    ) {
        val connectionsClient = Nearby.getConnectionsClient(context)
        connectionsClient.stopAdvertising()
    }

    internal fun startAdvertising(
        context: Context,
    ) {
        val connectionsClient = Nearby.getConnectionsClient(context)

        connectionsClient.stopDiscovery()

        listener.invoke(AdvertiseStatus.Loading)

        val advertisingOptions = AdvertisingOptions
            .Builder()
            .setStrategy(strategy)
            .build()
        connectionsClient
            .startAdvertising(
                localDataSource.username ?: "",
                context.packageName,
                getConnectionLifecycleCallback(context),
                advertisingOptions
            )
            .addOnSuccessListener {
                listener.invoke(AdvertiseStatus.StartFinishedWithSuccess)
            }
            .addOnFailureListener {
                listener.invoke(AdvertiseStatus.StartFinishedWithError(it))
            }
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

    private fun getConnectionLifecycleCallback(context: Context): ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                // Automatically accept the connection on both sides.
                listener.invoke(
                    AdvertiseStatus.ConnectionInitiated(
                        endpointId, connectionInfo.endpointName
                    )
                )
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        listener.invoke(
                            AdvertiseStatus.ConnectionResultStatus(
                                AdvertiseStatus.ConnectionResultStatus.CONNECTED
                            )
                        )
                    }

                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        listener.invoke(
                            AdvertiseStatus.ConnectionResultStatus(
                                AdvertiseStatus.ConnectionResultStatus.REJECTED
                            )
                        )
                    }

                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        listener.invoke(
                            AdvertiseStatus.ConnectionResultStatus(
                                AdvertiseStatus.ConnectionResultStatus.ERROR
                            )
                        )
                    }

                    else -> {
                        // no-op
                    }
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