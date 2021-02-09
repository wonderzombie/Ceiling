package irc

data class IrcMessage(val rawMessage: String) {
    private val userRegex = Regex("""\w[^!]+!~\w+@[\w.]+""")

    val type = IrcCommand.from(rawMessage)

    var header: String = ""
    var body: String = ""

    private fun parse(): IrcMessage {
        val trimmedMessage = if (rawMessage.startsWith(":")) rawMessage.drop(1) else rawMessage
        val splitMessage = trimmedMessage.split(":", limit = 2)
        println("| trimmed msg: $trimmedMessage")
        if (userRegex.containsMatchIn(splitMessage.first())) {
            println("| user msg")
            return parseUserMessage(trimmedMessage)
        }
        println("| server msg")
        return parseServerMessage(trimmedMessage)
    }

    private fun parseUserMessage(rawMessage: String): IrcMessage {
        when (type) {
            IrcCommand.PRIVMSG -> {
                rawMessage.split(":", limit = 2).let {
                    if (it.isEmpty()) return@let
                    header = it[0]
                    body = if (it.size == 1) "" else it[1]
                }
            }
            else -> listOf(rawMessage).also { println("WHAT IS THIS: $it") }
        }
        return this
    }

    private fun parseServerMessage(rawMessage: String): IrcMessage {
        rawMessage.split(Regex(":"), 2).let { tokens ->
            println("| server message tokens < $tokens >")
            header = tokens[0].trim()
            body = if (tokens.size == 1) "" else tokens[1]
        }
        return this
    }

    fun captured() = type.captures(rawMessage)

    companion object {
        fun from(rawMessage: String): IrcMessage = IrcMessage(rawMessage).parse()
    }
}