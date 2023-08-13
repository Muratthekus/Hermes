package me.thekusch.messager.wifi

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log

internal class WiFiDirectStatusReceiver(
    private val manager: WifiP2pManager? = null,
    private val channel: WifiP2pManager.Channel? = null,
    private val peersChangedBlock: WifiP2pManager.PeerListListener? = null,
    private val connectionInfoListener: WifiP2pManager.ConnectionInfoListener? = null
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                when (intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        Log.d("WIFISCANNER WIFI DIRECT","ENABLED")
                    }
                    else -> {
                        Log.d("WIFISCANNER WIFI DIRECT","NOT ENABLED")
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                manager?.requestPeers(channel, peersChangedBlock)
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                //
                // Respond to new connection or disconnections
                // Applications can use requestConnectionInfo(), requestNetworkInfo(),
                // or requestGroupInfo() to retrieve the current connection information.
                //
                manager?.let {
                    val networkInfo: NetworkInfo? =
                        intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO) as NetworkInfo?

                    if (networkInfo?.isConnected == true) {
                        manager.requestConnectionInfo(channel, connectionInfoListener)
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
                // Applications can use requestDeviceInfo() to retrieve the current connection information
            }
        }
    }
}