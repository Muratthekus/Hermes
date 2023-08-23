package me.thekusch.messager.permission

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class BluetoothPermissionRequestHandler(
    private val registry: ActivityResultRegistry,
    private val onNotGranted: (() -> Unit)
): DefaultLifecycleObserver {

    private val bluetoothPermissionKey = "bluetooth-permissions"
    lateinit var requestBluetoothPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        requestBluetoothPermissionLauncher =
            registry.register(
                bluetoothPermissionKey, owner,
                ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                if (result.isEmpty())
                    return@register
                val permissionResult = result.getOrDefault(bluetoothPermissionKey,false)
                if(permissionResult.not())
                    onNotGranted()
            }
    }
}