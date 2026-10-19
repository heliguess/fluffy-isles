package pet.itpuppy.fluffyisles.collectors

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import pet.itpuppy.fluffyisles.api.ActionBarAPI
import pet.itpuppy.fluffyisles.collectors.BossbarCollector.Segment
import pet.itpuppy.fluffyisles.utils.helpers.ClientUtils
import kotlin.jvm.optionals.getOrNull

object ActionBarCollector {
    private const val SPACES_FONT = "mythichud:spaces"

    private var cached: List<Segment> = listOf()
    private var parsedText: String? = null
    private var parsed: List<Segment> = listOf()
    private var lastTime = 0

    var lastRaw: Component? = null
        private set
    var lastReceivedAt = 0L
        private set

    private fun collect(component: Component): List<Segment> {
        val json = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, component).result().getOrNull()
            ?: return listOf()
        val result = mutableListOf<Segment>()
        flatten(json, null, null, result)
        return result
    }

    private fun segmentsFor(message: Component): List<Segment> {
        val text = message.string
        if (text != parsedText) {
            parsedText = text
            parsed = collect(message)
        }
        return parsed
    }

    fun tick() {
        val gui = ClientUtils.gui
        val time = gui.overlayMessageTime
        val message = gui.overlayMessageString

        if (time > lastTime && message != null) {
            lastRaw = message
            lastReceivedAt = System.currentTimeMillis()
            cached = segmentsFor(message)
            ActionBarAPI.onMessage(cached)
        } else if (time <= 0) {
            cached = listOf()
        }
        lastTime = time
    }

    fun getFull(): List<Segment> {
        return cached
    }

    private fun flatten(element: JsonElement, font: String?, color: String?, out: MutableList<Segment>) {
        if (element.isJsonPrimitive) {
            add(element.asString, font, color, out)
        } else if (element.isJsonObject) {
            val obj = element.asJsonObject
            val ownFont = obj.get("font")?.asString ?: font
            val ownColor = obj.get("color")?.asString ?: color
            obj.get("text")?.asString?.let { add(it, ownFont, ownColor, out) }
            obj.getAsJsonArray("extra")?.forEach { flatten(it, ownFont, ownColor, out) }
        }
    }

    private fun add(text: String, font: String?, color: String?, out: MutableList<Segment>) {
        if (text.isEmpty() || font == SPACES_FONT) return
        out.add(Segment(font ?: "", color, text))
    }
}