package me.thekusch.messager.permission

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class LocationPermissionRequestHandler(
    private val registry: ActivityResultRegistry,
    private val onPermissionResult: (Boolean) -> Unit,
): DefaultLifecycleObserver {

    private val requestEnableLocationKey = "requestEnableLocationKey"
    private val requestLocationPermission = "requestLocationPermission"

    lateinit var requestEnableLocation: ActivityResultLauncher<IntentSenderRequest>

    lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        requestEnableLocation = registry.register(
            requestEnableLocationKey, owner,
            ActivityResultContracts.StartIntentSenderForResult()
        ) {}

        requestLocationPermissionLauncher =
            registry.register(
                requestLocationPermission, owner,
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                var isAllPermitted: Boolean = true
                for (permission in permissions) {
                    if (!permission.value) {
                        isAllPermitted = false
                        break
                    }
                }
                //isAllPermitted = permissions.getOrDefault(requestLocationPermission,false)
                onPermissionResult(isAllPermitted)
            }
    }
}