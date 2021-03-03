package irc

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
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

        runBlocking {
            client.start()
            delay(100)
            client.stop()
        }

        assertThat(fakeConn.received).contains("hello")
    }

    @Test
    fun client_usesChannel_receiveOk() = runBlocking {
        val fakeConn = FakeConn()
        val client = AsyncIrcClient(fakeConn)

        val message = ":foo.bar.net 001 thumbkin :Welcome to your demise"
        fakeConn.addToSendList(message)

        var clientReceived: String?

        runBlocking {
            withTimeout(2000L) {
                client.start()
                clientReceived =
                    client.serverFeed.receive()
            }
        }

        assertThat(clientReceived).isEqualTo(message)
        assertThat(fakeConn.toSend).isEmpty()
    }
}