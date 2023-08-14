package me.thekusch.messager.wifi.permission

import android.Manifest
import androidx.fragment.app.FragmentActivity
import me.thekusch.messager.util.hasLocationPermission
import me.thekusch.messager.util.isLocationEnabled

internal class LocationManager(
    private val activity: FragmentActivity
) {

    var isLocationAccessible: Boolean
        get() {
            return activity.hasLocationPermission() && activity.isLocationEnabled()
        }

    private var locationEnableRequestBuilder = LocationEnableRequestBuilder()
    private var locationPermissionRequestHandler: LocationPermissionRequestHandler

    init {
        isLocationAccessible = activity.hasLocationPermission() && activity.isLocationEnabled()
        locationEnableRequestBuilder.init(activity)
        locationPermissionRequestHandler = LocationPermissionRequestHandler(activity.activityResultRegistry) {
            requestEnableLocation()
        }
        activity.lifecycle.addObserver(locationPermissionRequestHandler)
    }

    private fun requestEnableLocation() {
        locationEnableRequestBuilder.onRegisterActivityToEnableLocation = { it ->
            locationPermissionRequestHandler.requestEnableLocation.launch(it)
        }
        locationEnableRequestBuilder.requestToEnableLocation(activity) {
            // todo(murat) handle error
        }
    }

    fun checkLocationPermission() {
        when {
            activity.hasLocationPermission() -> {
                if (activity.isLocationEnabled().not()) {
                    requestEnableLocation()
                }
            }

            else -> {
                locationPermissionRequestHandler
                    .requestLocationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
            }
        }
    }

}