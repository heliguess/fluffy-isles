package pet.itpuppy.fluffyisles.api

enum class ChatRank(val displayName: String, val symbol: String) {
    ADVENTURER("Adventurer", "\uE046"),
    FOUNDER("Founder", "\uE045"),
    GAME_TESTER("Game Tester", "\uE044"),
    STAFF("Staff", "\uE042"),
    ADMINISTRATOR("Administrator", "\uE040"),
    OWNER("Owner", "\uE049"),
    DEVELOPER("Developer", "\uE041"),
    ARTIST("Artist", "\uE047"),
    BUILDER("Builder", "\uE043"),
    BUILD_LEAD("Build Lead", "\uE048");

    val commandName: String get() = name.lowercase()

    companion object {
        private val bySymbol = entries.associateBy { it.symbol }

        fun fromSymbol(symbol: String): ChatRank? = bySymbol[symbol]
        fun fromName(name: String): ChatRank? = entries.find { it.commandName == name.lowercase() }
    }
}
