package pet.itpuppy.fluffyisles.collectors

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import pet.itpuppy.fluffyisles.FluffyIsles
import kotlin.jvm.optionals.getOrNull

object BossbarCollector {
    data class Segment(val font: String, val color: String?, val text: String)

    private const val SPACES_FONT = "mythichud:spaces"

    private var cached: List<Segment> = listOf()
    private var lastNames: List<Component> = listOf()
    private var lastKey: String? = null

    private fun events() = FluffyIsles.client.gui.bossOverlay.events.values

    private fun collect(): List<Segment> {
        val result = mutableListOf<Segment>()
        events().forEach { event ->
            val json = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, event.name).result().getOrNull()
                ?: return@forEach
            flatten(json, null, null, result)
        }
        return result
    }

    private fun sameNames(): Boolean {
        val events = events()
        if (events.size != lastNames.size) return false

        var index = 0
        for (event in events) {
            if (event.name !== lastNames[index++]) return false
        }
        return true
    }

    fun tick() {
        if (sameNames()) return
        lastNames = events().map { it.name }

        val key = lastNames.joinToString("|") { it.string }
        if (key == lastKey) return
        lastKey = key

        cached = collect()
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