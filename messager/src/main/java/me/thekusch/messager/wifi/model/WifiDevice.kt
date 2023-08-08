package me.thekusch.messager.wifi.model

public data class WifiDevice(
    val id: String,
    val name: String,
    val deviceType: String,
    val status: DeviceStatus,
    val isGroupOwner: Boolean = false,
) {
    public enum class DeviceStatus {
        CONNECTED,
        INVITED,
        FAILED,
        AVAILABLE,
        UNAVAILABLE;

        internal companion object {
            fun getDeviceStatusByOrdinal(ordinal: Int): DeviceStatus {
                return values().find { it.ordinal == ordinal } ?: UNAVAILABLE
            }
        }
    }
}