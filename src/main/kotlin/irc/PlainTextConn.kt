package irc

import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket

class PlainTextConn(addr: InetAddress = InetAddress.getLocalHost(), port: Int = 6667) : Connection {
    val socket = Socket(addr, port)
    val writer = PrintWriter(socket.getOutputStream())

    private val reader = InputStreamReader(socket.getInputStream())

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

class FakeConn(public var received: List<String>, public var toSend: List<String>) : Connection {
    override fun sendMsg(message: String) {
        received += received
    }

    override fun readLine(): String {
        val ln = toSend.first()
        toSend = toSend.minus(ln)
        return ln
    }

}

interface Connection {
    fun sendMsg(message: String)
    fun readLine(): String
}
