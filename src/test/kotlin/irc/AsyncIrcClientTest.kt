package irc

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Test

class AsyncIrcClientTest {

    @Test
    fun client_usesChannel_sendOk() = runBlocking<Unit> {
        val fakeConn = FakeConn()
        val client = AsyncIrcClient(fakeConn)

        runBlocking {
                client.start()
        }
        client.channel.send("hello")

        assertThat(fakeConn.received).contains("hello")
    }

    @Test
    fun client_usesChannel_receiveOk() = runBlocking {
        val fakeConn = FakeConn()
        val client = AsyncIrcClient(fakeConn)

        val message = ":foo.bar.net 001 thumbkin :Welcome to your demise"
        fakeConn.addToSendList(message)

        var clientReceived: String? = ""

        launch {
            withTimeoutOrNull(100L) {
                client.start()
                clientReceived = withTimeoutOrNull(100L) {
                    client.channel.receive()
                }
                client.stop()
            }
        }.join()

        assertThat(clientReceived).isEqualTo(message)
        assertThat(fakeConn.toSend).isEmpty()
    }
}