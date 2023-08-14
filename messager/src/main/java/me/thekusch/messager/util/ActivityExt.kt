package me.thekusch.messager.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import androidx.activity.ComponentActivity
import com.vmadalin.easypermissions.EasyPermissions

internal fun ComponentActivity.isLocationEnabled(): Boolean {
    val locationManager: LocationManager =
        this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

internal fun ComponentActivity.hasBluetoothAdvertisePermission() = EasyPermissions.hasPermissions(
    this,
    Manifest.permission.BLUETOOTH_ADVERTISE
)

internal fun ComponentActivity.hasBluetoothConnectPermission() = EasyPermissions.hasPermissions(
    this,
    Manifest.permission.BLUETOOTH_CONNECT
)

internal fun ComponentActivity.hasBluetoothScanPermission() = EasyPermissions.hasPermissions(
    this,
    Manifest.permission.BLUETOOTH_SCAN
)

internal fun Context.hasLocationPermission() = EasyPermissions.hasPermissions(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION
)

internal fun ComponentActivity.hasBluetoothPermissions(): Boolean {
    return hasBluetoothConnectPermission()
            && hasBluetoothScanPermission()
            && hasBluetoothAdvertisePermission()
}

internal fun ComponentActivity.hasAllRequiredPermission(): Boolean {
    return hasBluetoothConnectPermission() && hasLocationPermission()
            && hasBluetoothScanPermission() && hasBluetoothAdvertisePermission()
}