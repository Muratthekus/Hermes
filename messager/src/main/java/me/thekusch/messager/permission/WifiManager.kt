package me.thekusch.messager.permission

import android.Manifest
import androidx.fragment.app.FragmentActivity
import me.thekusch.messager.util.hasAccessWifiStatePermission

internal class WifiManager(
    private val activity: FragmentActivity,
    permissionNotGrantedHandler: () -> Unit
) {

    var isWifiPermissionGranted: Boolean
        get() {
            return activity.hasAccessWifiStatePermission()
        }

    private var permissionRequestHandler: WifiPermissionRequestHandler

    init {
        isWifiPermissionGranted = activity.hasAccessWifiStatePermission()
        permissionRequestHandler = WifiPermissionRequestHandler(
            activity.activityResultRegistry,
            permissionNotGrantedHandler
        )
        activity.lifecycle.addObserver(permissionRequestHandler)
    }

    fun checkWifiPermission() {
        when {
            activity.hasAccessWifiStatePermission() -> {

            }

            else -> {
                permissionRequestHandler.requestAccessWifiStatePermissionLauncher.launch(
                    Manifest.permission.ACCESS_WIFI_STATE
                )
            }
        }
    }

}