package irc

class IrcMessage private constructor(val rawMessage: String) {
    private val userRegex = Regex("""\w[^!]+!~\w+@\w+""")

    val type = IrcCommand.from(rawMessage)

    var header: String = ""
    var body: String = ""
    var footer: String = ""

    private fun parse(): IrcMessage {
        val trimmedMessage = if (rawMessage.startsWith(":")) rawMessage.drop(1) else rawMessage
        val splitMessage = trimmedMessage.split(":", limit = 2)
//        println("| raw msg: $rawMessage")
        println("| trimmed msg: $trimmedMessage")
        if (splitMessage.first().matches(userRegex)) {
            println("| user msg")
            return parseUserMessage(trimmedMessage)
        }
        println("| server msg")
        return parseServerMessage(trimmedMessage)
    }

    private fun parseUserMessage(rawMessage: String): IrcMessage {
        when (type) {
            IrcCommand.PRIVMSG -> {
                val privMsgParts = rawMessage.split(":", limit = 2)
                println("| privmsgparts $privMsgParts")
                header = privMsgParts[0]
                body = privMsgParts[1]
                println("| user header < $header >")
                println("| user body < $body >")
            }
            else -> listOf(rawMessage).also { println("what is this: $it") }
        }
        return this
    }

    private fun parseServerMessage(rawMessage: String): IrcMessage {
        rawMessage.split(Regex(":"), 2).let {
            println("| server message parts < $it >")

            header = it.first().trim()
            if (it.size > 2) {
                body = it.drop(1).joinToString(separator = "\n").trim()
                footer = it.drop(2).joinToString(separator = "\n").trim()
            } else {
                body = it.drop(1).joinToString(separator = "\n").trim()
                footer = ""
            }
        }
        return this
    }

    fun captured() = type.captures(rawMessage)

    companion object {
        fun from(rawMessage: String): IrcMessage = IrcMessage(rawMessage).parse()
    }
}