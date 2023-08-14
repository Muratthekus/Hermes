package me.thekusch.messager

import androidx.fragment.app.FragmentActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.Strategy
import me.thekusch.messager.controller.Client
import me.thekusch.messager.controller.Server
import me.thekusch.messager.permission.BluetoothManager
import me.thekusch.messager.permission.LocationManager


//TODO(murat) handle 'Never Ask Again'
public class Hermes public constructor(
    private val activity: FragmentActivity
) {

    private var serverSocket: Server? = null
    private var clientSocket: Client? = null
    private var isServer: Boolean = false

    private var strategy = Strategy.P2P_CLUSTER

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

    private fun checkBluetoothPermissions() {
        bluetoothManager.checkBluetoothPermissions()
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions
            .Builder()
            .setStrategy(strategy)
            .build()

        Nearby.getConnectionsClient(activity)
            .startAdvertising(
                getLocalUserName(), SERVICE_ID, connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener { unused: Void? -> }
            .addOnFailureListener { e: Exception? -> }
    }

    private fun startDiscovery() {

    }
}