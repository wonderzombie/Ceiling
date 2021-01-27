package irc

import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket

class Connection(addr: InetAddress = InetAddress.getLocalHost(), port: Int = 6667) {
    val socket = Socket(addr, port)
    val writer = PrintWriter(socket.getOutputStream())

    private val reader = InputStreamReader(socket.getInputStream())

    fun sendMsg(message: String) {
        writer.write(message + "\r\n")
        writer.flush().also { println("-> $message") }
    }

    fun readLine(): String {
        if (reader.ready()) {
            val line = socket.getInputStream().bufferedReader().readLine()
            return line.also { println("<- $line") }
        }
        return ""
    }
}