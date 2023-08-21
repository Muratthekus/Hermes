package me.thekusch.messager.controller


public sealed interface BaseStatus{

    object Initial: BaseStatus

    public data class ConnectionInitiated(
        val endpointId: String,
        val endpointName: String
    ): BaseStatus

    public data class ConnectionResultStatus(val result: String): BaseStatus{
        public companion object {
            public const val CONNECTED = "CONNECTED"
            public const val ERROR = "ERROR"
            public const val REJECTED =  "REJECTED"
        }
    }

    public object Disconnected: BaseStatus
}