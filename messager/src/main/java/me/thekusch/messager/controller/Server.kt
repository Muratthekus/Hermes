package me.thekusch.messager.controller

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

internal class Server(private var port: Int): Thread() {

    var onMessageReceived: ((String) -> Unit)? = null
    lateinit var serverSocket: ServerSocket
    lateinit var inputStream: InputStream
    lateinit var outputStream: OutputStream
    lateinit var socket: Socket

    private lateinit var serverJob: Job

    override fun run() {
        super.run()
        serverSocket = ServerSocket(port)
        socket = serverSocket.accept()
        inputStream = socket.getInputStream()
        outputStream = socket.getOutputStream()

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        serverJob = CoroutineScope(Dispatchers.IO).launch {
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
    }

    fun clear() {
        serverJob.cancel()
    }

    fun sendMessage(message: ByteArray) {
        outputStream.write(message)
    }
}