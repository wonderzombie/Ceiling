package irc

import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket

class PlainTextConn(addr: InetAddress = InetAddress.getLocalHost(), port: Int = 6667) : Connection {
    private val socket = Socket(addr, port)
    private val writer = PrintWriter(socket.getOutputStream())
    private val bufferedReader = socket.getInputStream().bufferedReader()

    override val isConnected: Boolean
        get() = socket.isConnected

    override fun sendMsg(message: String) {
        writer.write(message + "\r\n").apply { writer.flush() }
        println("-> $message")
    }

    override fun readLine(): String =
        if (bufferedReader.ready()) {
            bufferedReader.readLine().also { println("<- $it") }
        } else ""
}

