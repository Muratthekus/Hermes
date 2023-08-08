package me.thekusch.messager.wifi.permission

import android.Manifest
import androidx.activity.ComponentActivity
import me.thekusch.messager.util.hasLocationPermission
import me.thekusch.messager.util.isLocationEnabled
import kotlin.properties.Delegates

class LocationManager(
    private val activity: ComponentActivity
) {

    var isLocationAccessible: Boolean
        get() {
            return activity.hasLocationPermission() && activity.isLocationEnabled()
        }

    private var locationTracker = LocationRequestBuilder()
    private var locationRequestHandler: LocationRequestHandler

    init {
        isLocationAccessible = activity.hasLocationPermission() && activity.isLocationEnabled()
        locationTracker.init(activity)
        locationRequestHandler = LocationRequestHandler(activity.activityResultRegistry) {
            requestEnableLocation()
        }
        activity.lifecycle.addObserver(locationRequestHandler)
    }

    fun requestEnableLocation() {
        locationTracker.onRegisterActivityToEnableLocation = { it ->
            locationRequestHandler.requestEnableLocation.launch(it)
        }
        locationTracker.requestToEnableLocation(activity) {
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
                locationRequestHandler
                    .requestLocationPermissionLauncher.launch(
                        LOCATION_PERM
                    )
            }
        }
    }

    companion object {
        private const val LOCATION_PERM = Manifest.permission.ACCESS_FINE_LOCATION
    }
}