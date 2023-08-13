package me.thekusch.messager.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import androidx.activity.ComponentActivity
import com.vmadalin.easypermissions.EasyPermissions

fun ComponentActivity.isLocationEnabled(): Boolean {
    val locationManager: LocationManager =
        this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

fun Context.hasLocationPermission() = EasyPermissions.hasPermissions(
    this,
    Manifest.permission.ACCESS_FINE_LOCATION
)
