package me.thekusch.messager

import android.content.Context
import androidx.fragment.app.FragmentActivity
import me.thekusch.messager.controller.Advertise
import me.thekusch.messager.controller.BaseStatus
import me.thekusch.messager.controller.Discovery
import me.thekusch.messager.datasource.LocalDataSource
import me.thekusch.messager.permission.BluetoothManager
import me.thekusch.messager.permission.LocationManager
import me.thekusch.messager.permission.WifiManager


internal typealias DiscoveryStatusListener = ((BaseStatus) -> Unit)
internal typealias AdvertiseStatusListener = ((BaseStatus) -> Unit)

public class Hermes public constructor(
    private val activity: FragmentActivity,
    permissionNotGrantedHandler: () -> Unit
) {

    private var isServer: Boolean = false

    public var discoveryStatusListener: DiscoveryStatusListener? = null
    public var advertiseStatusListener: AdvertiseStatusListener? = null
    private var locationManager = LocationManager(activity, permissionNotGrantedHandler)
    private var bluetoothManager = BluetoothManager(activity, permissionNotGrantedHandler)
    private var wifiManager = WifiManager(activity, permissionNotGrantedHandler)
    private var advertise = Advertise()
    private var discovery = Discovery()
    private val localDataSource = LocalDataSource

    init {
        checkLocationPermission()
        checkBluetoothPermissions()
        checkWifiPermissions()
        localDataSource.init(activity.applicationContext)
    }

    public fun setUserName(username: String) {
        localDataSource.username = username
    }

    public fun acceptConnection(
        endpointId: String,
        context: Context
    ) = advertise.acceptConnectionRequest(context, endpointId)


    public fun rejectConnection(
        endpointId: String,
        context: Context
    ) = advertise.rejectConnectionRequest(context, endpointId)

    public fun startAdvertising() {
        requireNotNull(advertiseStatusListener) {
            "a value should have been set to listener"
        }
        advertise.listener = advertiseStatusListener!!
        advertise.startAdvertising(activity)
    }

    public fun startDiscovery() {
        requireNotNull(discoveryStatusListener) {
            "a value should have been set to listener"
        }

        discovery.listener = discoveryStatusListener!!
        discovery.startDiscovery(activity)
    }

    private fun checkLocationPermission() {
        locationManager.checkLocationPermission()
    }

    private fun checkBluetoothPermissions() {
        bluetoothManager.checkBluetoothPermissions()
    }

    private fun checkWifiPermissions() {
        wifiManager.checkWifiPermission()
    }
}
