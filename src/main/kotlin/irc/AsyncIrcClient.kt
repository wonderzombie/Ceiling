package irc

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class AsyncIrcClient(private val conn: Connection, val nick: String = "thumbkin") {
    private var writeJob: Job? = null
    private var readJob: Job? = null
    val channel: Channel<String> = Channel(10)
    private var started: Boolean = false
    lateinit var mainJob: Job

    suspend fun start() {
        started = true

        mainJob = GlobalScope.launch {
            readJob = launch { readFromConnection() }
            writeJob = launch { writeToConnection() }
        }
    }

    private suspend fun readFromConnection() = withTimeoutOrNull(1000L) {
        val ln = conn.readLine()
        if (ln.isEmpty()) {
            println("...empty")
            return@withTimeoutOrNull
        }
        println("!!! recv")
        channel.send(ln)
    }

    private suspend fun writeToConnection() = withTimeoutOrNull(1000L) {
        channel.receive().let { msg ->
            println("!!! sendjob $msg")
            conn.sendMsg(msg)
        }
    }

    fun stop() {
        println("stop() called")
        readJob?.cancel()
        writeJob?.cancel()
        mainJob.cancel()
        channel.close()
    }
}