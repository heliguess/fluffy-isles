package pet.itpuppy.fluffyisles.utils

import net.minecraft.network.chat.Component
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

object TextUtils {
    private const val GRADIENT_START = 0x6BFF6F
    private const val GRADIENT_END = 0xEDFF87

    fun number(value: Long): String = String.format(Locale.ROOT, "%,d", value)

    fun parseAbbreviated(text: String): Long? {
        val clean = text.trim().replace(",", "")
        val multiplier = when (clean.lastOrNull()?.lowercaseChar()) {
            'k' -> 1_000.0
            'm' -> 1_000_000.0
            'b' -> 1_000_000_000.0
            else -> 1.0
        }
        val digits = if (multiplier == 1.0) clean else clean.dropLast(1)
        return digits.toDoubleOrNull()?.let { (it * multiplier).toLong() }
    }

    fun duration(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }

    fun titleCase(text: String): String =
        text.lowercase().split(" ").joinToString(" ") { word -> word.replaceFirstChar { it.uppercaseChar() } }

    fun gradient(text: String, boldLength: Int = 0): Component {
        val last = (text.length - 1).coerceAtLeast(1)
        val chars = text.mapIndexed { index, char ->
            val t = 1.0 - abs(2.0 * index / last - 1.0)
            Comp.of(char.toString()).withStyle { it.withColor(blend(t)).withBold(index < boldLength) }
        }
        return Comp.build(*chars.toTypedArray())
    }

    private fun blend(t: Double): Int {
        fun channel(shift: Int): Int {
            val from = (GRADIENT_START shr shift) and 0xFF
            val to = (GRADIENT_END shr shift) and 0xFF
            return (from + (to - from) * t).roundToInt()
        }
        return (channel(16) shl 16) or (channel(8) shl 8) or channel(0)
    }
}