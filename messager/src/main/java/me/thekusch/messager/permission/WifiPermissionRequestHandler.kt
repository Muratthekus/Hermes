package me.thekusch.messager.permission

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class WifiPermissionRequestHandler(
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {

    lateinit var requestAccessWifiStatePermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        requestAccessWifiStatePermissionLauncher =
            registry.register(
                "access-wifi-state-permissions", owner,
                ActivityResultContracts.RequestPermission()
            ) {
                //TODO(murat) handle never ask again
            }
    }
}