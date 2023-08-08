package me.thekusch.messager.controller

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.Executors

internal class Client(
    private val hostAddress: InetAddress,
    private val port: Int
): Thread() {

    var onMessageReceived: ((String) -> Unit)? = null
    var hostAdd: String? = hostAddress.hostAddress
    lateinit var inputStream: InputStream
    lateinit var outputStream: OutputStream
    var socket: Socket = Socket()

    private lateinit var clientJob: Job

    override fun run() {
        super.run()
        socket.connect(InetSocketAddress(hostAdd,port),500)
        inputStream = socket.getInputStream()
        outputStream = socket.getOutputStream()

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        clientJob = CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(1024)
            var bytes = 0

            while (socket.isClosed.not()) {
                bytes = inputStream.read(buffer)
                if (bytes > 0) {
                    val finalBytes = bytes
                    //handler.post {
                        val message = String(buffer,0,finalBytes)
                        onMessageReceived?.invoke(message)
                    //}
                }
            }
        }

        /*executor.run {
            val buffer = ByteArray(1024)
            var bytes = 0

            while (socket.isClosed.not()) {
                bytes = inputStream.read(buffer)
                if (bytes > 0) {
                    val finalBytes = bytes
                    handler.post {
                        val message = String(buffer,0,finalBytes)
                        onMessageReceived?.invoke(message)
                    }
                }
            }
        }*/
    }

    fun clear() {
        clientJob.cancel()
    }

    fun sendMessage(message: ByteArray) {
        outputStream.write(message)
    }

}