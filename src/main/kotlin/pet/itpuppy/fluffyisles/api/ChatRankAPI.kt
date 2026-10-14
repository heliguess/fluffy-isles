package pet.itpuppy.fluffyisles.api

object ChatRankAPI {
    enum class Rank(val displayName: String, val symbol: String) {
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
    }

    private val bySymbol = Rank.entries.associateBy { it.symbol }

    fun fromSymbol(symbol: String): Rank? = bySymbol[symbol]
    fun fromName(name: String): Rank? = Rank.entries.find { it.commandName == name.lowercase() }
}