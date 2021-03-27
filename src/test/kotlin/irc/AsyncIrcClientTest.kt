package irc

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test

class AsyncIrcClientTest {
    @Test
    fun client_usesChannel_sendOk() = runBlocking {
        val fakeConn = FakeConn()
        fakeConn.addToSendList(":foo.bar.net 001 thumbkin :welcome")

        val client = AsyncIrcClient(fakeConn)
        client.clientInput.send("hello")

        withTimeout(3000L) {
            val job = launch { client.start() }
            delay(100)
            client.stop()
            job.join()
        }

        assertThat(fakeConn.received).contains("hello")
    }

    @Test
    fun client_usesChannel_receiveOk() {
        val fakeConn = FakeConn()
        val welcomeMsg = ":foo.bar.net 001 thumbkin :Welcome to your demise"
        fakeConn.addToSendList(welcomeMsg)

        val client = AsyncIrcClient(fakeConn)
        var clientReceived: String?


        runBlocking {
//        withTimeout(3000L) {
            val job = launch { client.start() }
            clientReceived =
                client.serverFeed.receive()
            client.stop()
            job.join()
        }

        assertThat(clientReceived).isEqualTo(welcomeMsg)
        assertThat(fakeConn.toSend).isEmpty()
    }
}