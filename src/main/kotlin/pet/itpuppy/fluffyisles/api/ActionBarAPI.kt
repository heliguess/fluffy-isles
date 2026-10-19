package pet.itpuppy.fluffyisles.api

import pet.itpuppy.fluffyisles.collectors.BossbarCollector.Segment
import pet.itpuppy.fluffyisles.utils.TextUtils.parseAbbreviated

object ActionBarAPI {
    sealed interface Message {
        data class ExpProgress(val current: Long, val required: Long) : Message
        data class RestedTime(val gainSeconds: Double, val totalSeconds: Double) : Message
    }

    private val EXP_PROGRESS = Regex("""^([\d.,]+[kKmMbB]?)\s*/\s*([\d.,]+[kKmMbB]?)\s+Exp$""")
    private val RESTED_TIME = Regex("""^\+([\d.]+)s\s+Rested Exp time\s*\((.+)\)$""")
    private val DURATION_PART = Regex("""(\d+(?:\.\d+)?)\s*([hms])""", RegexOption.IGNORE_CASE)

    var text: String? = null
        private set
    var segments: List<Segment> = emptyList()
        private set

    var exp: Long? = null
        private set
    var expRequired: Long? = null
        private set
    var restedGainSeconds: Double? = null
        private set
    var restedTotalSeconds: Double? = null
        private set

    private val messageListeners = mutableListOf<(Message) -> Unit>()
    private val rawListeners = mutableListOf<(String) -> Unit>()

    fun onParsed(listener: (Message) -> Unit) {
        messageListeners.add(listener)
    }

    fun onRaw(listener: (String) -> Unit) {
        rawListeners.add(listener)
    }

    fun reset() {
        text = null
        segments = emptyList()
        exp = null
        expRequired = null
        restedGainSeconds = null
        restedTotalSeconds = null
    }

    fun onMessage(newSegments: List<Segment>) {
        segments = newSegments
        val joined = newSegments.joinToString("") { it.text }.trim()
        text = joined

        rawListeners.forEach { it(joined) }

        val message = parse(joined) ?: return
        apply(message)
        messageListeners.forEach { it(message) }
    }

    private fun apply(message: Message) {
        when (message) {
            is Message.ExpProgress -> {
                exp = message.current
                expRequired = message.required
            }
            is Message.RestedTime -> {
                restedGainSeconds = message.gainSeconds
                restedTotalSeconds = message.totalSeconds
            }
        }
    }

    private fun parse(text: String): Message? = parseProgress(text) ?: parseRested(text)

    private fun parseProgress(text: String): Message.ExpProgress? {
        val match = EXP_PROGRESS.find(text) ?: return null
        val current = parseAbbreviated(match.groupValues[1]) ?: return null
        val required = parseAbbreviated(match.groupValues[2]) ?: return null
        return Message.ExpProgress(current, required)
    }

    private fun parseRested(text: String): Message.RestedTime? {
        val match = RESTED_TIME.find(text) ?: return null
        val gain = match.groupValues[1].toDoubleOrNull() ?: return null
        val total = parseDuration(match.groupValues[2]) ?: return null
        return Message.RestedTime(gain, total)
    }

    private fun parseDuration(text: String): Double? {
        val parts = DURATION_PART.findAll(text).toList()
        if (parts.isEmpty()) return null

        return parts.sumOf {
            val value = it.groupValues[1].toDouble()
            when (it.groupValues[2].lowercase()) {
                "h" -> value * 3600
                "m" -> value * 60
                else -> value
            }
        }
    }
}