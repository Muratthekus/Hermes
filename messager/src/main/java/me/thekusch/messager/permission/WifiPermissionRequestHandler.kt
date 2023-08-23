package me.thekusch.messager.permission

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class WifiPermissionRequestHandler(
    private val registry: ActivityResultRegistry,
    private val onNotGranted: () -> Unit
) : DefaultLifecycleObserver {

    private val accessWifiStatePermission = "accessWifiStatePermission"
    lateinit var requestAccessWifiStatePermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        requestAccessWifiStatePermissionLauncher =
            registry.register(
                accessWifiStatePermission, owner,
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted.not())
                    onNotGranted()
            }
    }
}