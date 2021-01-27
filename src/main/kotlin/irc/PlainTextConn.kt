package irc

import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket

class PlainTextConn(addr: InetAddress = InetAddress.getLocalHost(), port: Int = 6667) : Connection {
    val socket = Socket(addr, port)
    val writer = PrintWriter(socket.getOutputStream())

    private val reader = InputStreamReader(socket.getInputStream())
    override val isConnected: Boolean
        get() = socket.isConnected

    override fun sendMsg(message: String) {
        writer.write(message + "\r\n")
        writer.flush().also { println("-> $message") }
    }

    override fun readLine(): String {
        if (reader.ready()) {
            val line = socket.getInputStream().bufferedReader().readLine()
            return line.also { println("<- $line") }
        }
        return ""
    }
}

