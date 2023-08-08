package me.thekusch.messager.wifi.permission

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class LocationRequestHandler(
    private val registry: ActivityResultRegistry,
    private val onLocationGranted: () -> Unit,
): DefaultLifecycleObserver {

    lateinit var requestEnableLocation: ActivityResultLauncher<IntentSenderRequest>

    lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        requestEnableLocation = registry.register(
            "key1", owner,
            ActivityResultContracts.StartIntentSenderForResult()
        ) {}

        requestLocationPermissionLauncher =
            registry.register(
                "key2", owner,
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted)
                    onLocationGranted()
            }
    }
}