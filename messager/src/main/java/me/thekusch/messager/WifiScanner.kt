package me.thekusch.messager

import android.annotation.SuppressLint
import android.net.wifi.p2p.*
import androidx.fragment.app.FragmentActivity
import me.thekusch.messager.controller.Client
import me.thekusch.messager.controller.Server
import me.thekusch.messager.wifi.permission.BluetoothManager
import me.thekusch.messager.wifi.permission.LocationManager

//TODO(murat) handle 'Never Ask Again'
public class WiFiScanner public constructor(
    activity: FragmentActivity
) {

    private var serverSocket: Server? = null
    private var clientSocket: Client? = null
    private var isServer: Boolean = false

    private var locationManager: LocationManager
    private var bluetoothManager: BluetoothManager


    init {
        locationManager = LocationManager(activity)
        bluetoothManager = BluetoothManager(activity)
        checkLocationPermission()
        checkBluetoothPermissions()
    }


    private fun checkLocationPermission() {

        locationManager.checkLocationPermission()
    }

    fun checkBluetoothPermissions() {
        bluetoothManager.checkBluetoothPermissions()
    }

    public companion object {
        public var LOCATION_PERMISSION_CODE: Int = 1001
    }
}