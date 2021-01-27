package irc

enum class IrcCommand(private val rx: Regex) {
    UNKNOWN(Regex("^$")),
    SERVER(Regex("""^:(.+) (\d{3}) (\w+) :?(.*)""")),
    PING(Regex("""^PING :(.+)""")),
    JOIN(Regex("""JOIN (#\w+) (\w+)""")),
    PRIVMSG((Regex(""":(.*) PRIVMSG (#\w+) :?(.*)""")));

    companion object {
        fun from(message: String): IrcCommand =
            values().find { enumVal ->
                enumVal.rx.also { println("it $it in $message") }.matches(message)
                    .also { println("...$it") }
            } ?: UNKNOWN

    }

    fun matches(message: String): Boolean = rx.containsMatchIn(message)

    fun captures(message: String): MatchResult? = rx.find(message)

    fun begins(message: String): Boolean = rx.find(message)?.range?.start == 0
}

