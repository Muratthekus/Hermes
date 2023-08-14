package me.thekusch.messager.wifi.permission

import android.Manifest
import android.os.Build
import androidx.fragment.app.FragmentActivity
import me.thekusch.messager.util.hasBluetoothAdvertisePermission
import me.thekusch.messager.util.hasBluetoothConnectPermission
import me.thekusch.messager.util.hasBluetoothPermissions
import me.thekusch.messager.util.hasBluetoothScanPermission

internal class BluetoothManager(
    private val activity: FragmentActivity
) {

    var isAllBluetoothPermissionsGranted: Boolean
        get() {
            return activity.hasBluetoothScanPermission()
                    && activity.hasBluetoothAdvertisePermission()
                    && activity.hasBluetoothConnectPermission()
        }
    private var permissionRequestHandler: BluetoothPermissionRequestHandler

    init {
        isAllBluetoothPermissionsGranted = activity.hasBluetoothScanPermission()
                && activity.hasBluetoothAdvertisePermission()
                && activity.hasBluetoothConnectPermission()

        permissionRequestHandler =
            BluetoothPermissionRequestHandler(activity.activityResultRegistry)
        activity.lifecycle.addObserver(permissionRequestHandler)
    }

    fun getBluetoothPermissionList(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
        } else {
            emptyArray()
        }
    }

    //TODO(murat) user can accept one permission not the rest of
    // handle this situation and request only not allowed permssions
    fun checkBluetoothPermissions() {
        when {
            activity.hasBluetoothPermissions() -> {

            }

            else -> {
                permissionRequestHandler
                    .requestBluetoothPermissionLauncher.launch(
                        getBluetoothPermissionList()
                    )
            }
        }
    }
}