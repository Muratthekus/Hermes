package me.thekusch.messager

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.*
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat.getSystemService
import me.thekusch.messager.controller.Client
import me.thekusch.messager.controller.Server
import me.thekusch.messager.wifi.WiFiDirectStatusReceiver
import me.thekusch.messager.wifi.model.Status
import me.thekusch.messager.wifi.model.Status.ConnectionProcess.Companion.CONNECTED_AS_CLIENT
import me.thekusch.messager.wifi.model.Status.ConnectionProcess.Companion.CONNECTED_AS_HOST
import me.thekusch.messager.wifi.model.Status.ConnectionProcess.Companion.CONNECTION_FAILED
import me.thekusch.messager.wifi.model.Status.DiscoverProcess.Companion.FAILED
import me.thekusch.messager.wifi.model.Status.DiscoverProcess.Companion.FINISHED
import me.thekusch.messager.wifi.model.Status.DiscoverProcess.Companion.RUNNING
import me.thekusch.messager.wifi.model.WifiDevice
import me.thekusch.messager.wifi.permission.LocationManager
import java.net.InetAddress
import kotlin.properties.Delegates

//TODO(murat) handle permission's all states
@SuppressLint("MissingPermission")
public class WiFiScanner public constructor(
    activity: ComponentActivity
) {

    private var isDiscoveringPeers: Boolean = false

    private var manager by Delegates.notNull<WifiP2pManager>()
    private var channel: WifiP2pManager.Channel? = null

    private var receiver: BroadcastReceiver? = null

    private var serverSocket: Server? = null
    private var clientSocket: Client? = null
    private var isServer: Boolean = false

    private var wifiDevices: List<WifiDevice> = listOf()

    public var onP2pStatusChange: ((Status<Any>) -> Unit)? = null

    private var locationPermissionHandler: LocationManager


    init {
        val wifiManager = Manager()
        wifiManager.init(activity)
        locationPermissionHandler = LocationManager(activity)
        checkLocationPermission()
    }


    private fun checkLocationPermission() {
        locationPermissionHandler.checkLocationPermission()
    }

    /*
     * The onSuccess() method only notifies that the discovery process succeeded
     * and does not provide any information about the actual peers that it discovered, if any.
     *
     * If the discovery process succeeds and detects peers,
     * the system broadcasts the WIFI_P2P_PEERS_CHANGED_ACTION intent,
     * which we can listen for in a broadcast receiver to get a list of peers.
     */
    public fun discoverOthers() {
        if (locationPermissionHandler.isLocationAccessible)
            return

        if (isDiscoveringPeers.not()) {
            isDiscoveringPeers = true
            onP2pStatusChange?.invoke(
                Status.DiscoverProcess(RUNNING)
            )
            manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onFailure(p0: Int) {
                    isDiscoveringPeers = false
                    onP2pStatusChange?.invoke(
                        Status.DiscoverProcess(FAILED)
                    )
                }

                override fun onSuccess() {
                    isDiscoveringPeers = false
                    onP2pStatusChange?.invoke(
                        Status.DiscoverProcess(FINISHED)
                    )
                }
            })
        }
    }

    public fun connectToPeers(device: WifiDevice) {
        if (locationPermissionHandler.isLocationAccessible.not())
            return

        val config = WifiP2pConfig()
        config.apply {
            deviceAddress = device.id
            wps.setup = WpsInfo.PBC
        }

        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onFailure(p0: Int) {
                // TODO(murat) handle failure
                onP2pStatusChange?.invoke(
                    Status.ConnectionProcess(CONNECTION_FAILED)
                )
            }

            override fun onSuccess() {
                // BroadcastReceiver notifies us. Ignore for now.
            }
        })

    }

    public fun stopReceiver(ctx: Context) {
        ctx.unregisterReceiver(receiver)
    }

    public fun startReceiver(ctx: Context) {
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            // Indicates a change in the list of available peers.
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            // Indicates the state of Wi-Fi Direct connectivity has changed.
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            // Indicates this device's details have changed.
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        ctx.registerReceiver(receiver, intentFilter)
    }

    public fun sendMessage() {
        if (isServer) {
            serverSocket?.sendMessage("server".toByteArray())
        } else {
            clientSocket?.sendMessage("client".toByteArray())
        }
    }


    private inner class Manager :
        WifiP2pManager.PeerListListener,
        WifiP2pManager.ConnectionInfoListener {

        fun init(ctx: Context) {
            manager = getSystemService(ctx, WifiP2pManager::class.java)!!
            channel = manager.initialize(ctx, Looper.getMainLooper(), null)
            channel?.also { channel ->
                receiver = WiFiDirectStatusReceiver(
                    manager,
                    channel,
                    peersChangedBlock = this@Manager,
                    connectionInfoListener = this@Manager
                )
            }
        }

        /**
         * Triggered when discover process finished successfully
         * */
        override fun onPeersAvailable(p0: WifiP2pDeviceList?) {
            val deviceList = p0?.deviceList?.map {
                WifiDevice(
                    id = it.deviceAddress,
                    name = it.deviceName,
                    deviceType = it.primaryDeviceType,
                    status = WifiDevice.DeviceStatus.getDeviceStatusByOrdinal(it.status),
                    isGroupOwner = it.isGroupOwner
                )
            } ?: emptyList()
            wifiDevices = deviceList
            onP2pStatusChange?.invoke(
                Status.PeersDiscovered(wifiDevices)
            )
        }

        override fun onConnectionInfoAvailable(p0: WifiP2pInfo?) {
            // String from WifiP2pInfo struct
            val groupOwnerAddress: InetAddress? = p0?.groupOwnerAddress

            // After the group negotiation, we can determine the group owner
            // (server).
            // TODO(murat) handle threads lifecycle with connection and other changes
            if (p0?.groupFormed == true && p0.isGroupOwner) {
                // Do whatever tasks are specific to the group owner.
                // One common case is creating a group owner thread and accepting
                // incoming connections.
                isServer = true
                serverSocket = Server(8080)
                serverSocket?.start()

                serverSocket?.onMessageReceived = {
                    onP2pStatusChange?.invoke(
                        Status.MessageReceived(it)
                    )
                }
                onP2pStatusChange?.invoke(
                    Status.ConnectionProcess(CONNECTED_AS_HOST)
                )
            } else if (p0?.groupFormed == true) {
                // The other device acts as the peer (client). In this case,
                // you'll want to create a peer thread that connects
                // to the group owner.
                isServer = false
                groupOwnerAddress?.let {
                    clientSocket = Client(groupOwnerAddress, 8080)
                    clientSocket?.start()

                    clientSocket?.onMessageReceived = {
                        onP2pStatusChange?.invoke(
                            Status.MessageReceived(it)
                        )
                    }
                    onP2pStatusChange?.invoke(
                        Status.ConnectionProcess(CONNECTED_AS_CLIENT)
                    )
                } ?: kotlin.run {
                    // TODO(murat) determine what will happen
                }
            }
        }
    }

    public companion object {
        public var LOCATION_PERMISSION_CODE: Int = 1001
    }
}