package me.thekusch.messager.permission

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class BluetoothPermissionRequestHandler(
    private val registry: ActivityResultRegistry,
): DefaultLifecycleObserver {

    lateinit var requestBluetoothPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        requestBluetoothPermissionLauncher =
            registry.register(
                "bluetooth-permissions", owner,
                ActivityResultContracts.RequestMultiplePermissions()
            ) { isGranted ->
                //TODO(murat) handle never ask again
            }
    }
}