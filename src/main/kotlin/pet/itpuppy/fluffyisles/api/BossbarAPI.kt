package pet.itpuppy.fluffyisles.api

import pet.itpuppy.fluffyisles.collectors.BossbarCollector
import pet.itpuppy.fluffyisles.collectors.BossbarCollector.Segment
import pet.itpuppy.fluffyisles.utils.TextUtils.parseAbbreviated

object BossbarAPI {
    enum class GuildMaterial(val displayName: String) {
        LUMBER("Lumber"),
        METAL("Metal"),
        GRAIN("Grain"),
        FISH("Fish"),
        SOULS("Souls");

        companion object {
            fun fromName(name: String): GuildMaterial? =
                entries.find { it.displayName.equals(name, ignoreCase = true) }
        }
    }

    sealed interface Gain {
        data class Exp(val skill: String, val amount: Long) : Gain
        data class Coins(val amount: Long) : Gain
        data class Reputation(val amount: Long) : Gain
        data class Material(val material: GuildMaterial, val amount: Long) : Gain
    }

    private const val TEXTURES = "mythichud:layout/isles-layout/textures"
    private const val HEALTH_NUMBER = "mythichud:layout/isles-layout/fonts/isles-health-number/text"
    private const val ENERGY_NUMBER = "mythichud:layout/isles-layout/fonts/isles-energy-number/text"
    private const val GEMS_NUMBER = "mythichud:layout/isles-layout/fonts/isles-gems/text"
    private const val COINS_NUMBER = "mythichud:layout/isles-layout/fonts/isles-coins/text"
    private const val QUEST_TITLE = "mythichud:layout/quest-layout/fonts/quest-hud/text_line_title"

    private val ULT_CHARGE_GLYPHS = 0xE001..0xE014
    private const val ULT_READY_GLYPH = 0xE04B

    private val EXP_SLOT_FONT = Regex("""^mythichud:popup/exp-feed/fonts/exp-(\d+)/text$""")
    private val XP_TEXT = Regex("""^\+([\d.,]+[kKmMbB]?)\s+(.+?)\s+Exp$""")
    private val SIMPLE_GAIN_TEXT = Regex("""^\+([\d.,]+[kKmMbB]?)\s+([A-Za-z]+)$""")

    var health: Int? = null
        private set
    var energy: Int? = null
        private set
    var gems: Int? = null
        private set
    var coins: Long? = null
        private set
    var questTitle: String? = null
        private set

    var ultimateCharge: Float? = null
        private set
    var ultimateReady: Boolean = false
        private set

    private val gainListeners = mutableListOf<(Gain) -> Unit>()

    private var lastSegments: List<Segment>? = null
    private var feed: List<String> = emptyList()
    private var feedInitialized = false

    fun onGain(listener: (Gain) -> Unit) {
        gainListeners.add(listener)
    }

    fun reset() {
        lastSegments = null
        feed = emptyList()
        feedInitialized = false
    }

    fun tick() {
        val segments = BossbarCollector.getFull()
        if (segments === lastSegments) return
        lastSegments = segments

        health = textOf(segments, HEALTH_NUMBER)?.toIntOrNull()
        energy = textOf(segments, ENERGY_NUMBER)?.toIntOrNull()
        gems = textOf(segments, GEMS_NUMBER)?.toIntOrNull()
        coins = textOf(segments, COINS_NUMBER)?.let(::parseAbbreviated)
        questTitle = textOf(segments, QUEST_TITLE)

        val glyphs = segments.filter { it.font == TEXTURES }.flatMap { segment -> segment.text.map { it.code } }
        ultimateCharge = glyphs.firstOrNull { it in ULT_CHARGE_GLYPHS }?.let {
            (it - ULT_CHARGE_GLYPHS.first).toFloat() / (ULT_CHARGE_GLYPHS.last - ULT_CHARGE_GLYPHS.first)
        }
        ultimateReady = ULT_READY_GLYPH in glyphs

        updateFeed(segments)
    }

    private fun textOf(segments: List<Segment>, font: String): String? =
        segments.filter { it.font == font }.joinToString("") { it.text }.ifEmpty { null }

    private fun readFeed(segments: List<Segment>): List<String> =
        segments
            .mapNotNull { segment -> EXP_SLOT_FONT.find(segment.font)?.let { it.groupValues[1].toInt() to segment.text } }
            .groupBy({ it.first }, { it.second })
            .toSortedMap()
            .values
            .map { it.joinToString("") }

    private fun updateFeed(segments: List<Segment>) {
        val current = readFeed(segments)
        if (!feedInitialized) {
            feed = current
            feedInitialized = true
            return
        }

        val fresh = newEntryCount(feed, current)
        feed = current

        current.take(fresh).asReversed().forEach { text ->
            val gain = parseGain(text) ?: return@forEach
            gainListeners.forEach { it(gain) }
        }
    }

    private fun parseGain(text: String): Gain? {
        XP_TEXT.find(text)?.let { match ->
            val amount = parseAbbreviated(match.groupValues[1]) ?: return null
            return Gain.Exp(match.groupValues[2], amount)
        }

        val match = SIMPLE_GAIN_TEXT.find(text) ?: return null
        val amount = parseAbbreviated(match.groupValues[1]) ?: return null
        val name = match.groupValues[2]
        return when {
            name.equals("Coins", ignoreCase = true) || name.equals("Coin", ignoreCase = true) -> Gain.Coins(amount)
            name.equals("Reputation", ignoreCase = true) -> Gain.Reputation(amount)
            else -> GuildMaterial.fromName(name)?.let { Gain.Material(it, amount) }
        }
    }

    private fun newEntryCount(previous: List<String>, current: List<String>): Int {
        for (shift in 0..current.size) {
            val rest = current.drop(shift)
            if (rest.size <= previous.size && rest == previous.take(rest.size)) return shift
        }
        return current.size
    }
}