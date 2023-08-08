package me.thekusch.messager.wifi.model


public sealed class Status<out T : Any?>() {

    public data class DiscoverProcess<out T : Any?>(val result: Int) : Status<T>() {
        public companion object {
            public const val RUNNING: Int = 0
            public const val FAILED: Int = -1
            public const val FINISHED: Int = 1
        }
    }

    public data class PeersDiscovered<out T : Any?>(
        public val wifiDevice: List<WifiDevice>
    ) : Status<T>()

    public data class ConnectionProcess<out T: Any?>(
        val result: Int
    ): Status<T>() {
        public companion object {
            public const val CONNECTED_AS_CLIENT: Int = 0
            public const val CONNECTION_FAILED: Int = -1
            public const val CONNECTED_AS_HOST: Int = 1
        }
    }

    public data class MessageReceived<out T: Any?>(
        val message: String
    ): Status<T>()

}