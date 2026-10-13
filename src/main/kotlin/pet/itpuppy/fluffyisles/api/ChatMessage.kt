package pet.itpuppy.fluffyisles.api

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import pet.itpuppy.fluffyisles.utils.FormatCodes

object ChatMessage {
    enum class Channel { GLOBAL, GUILD, PARTY, DM, DISCORD }

    class Parsed(
        val channel: Channel,
        val sender: String,
        val rank: ChatRank?,
        private val component: Component,
    ) {
        val guild: String? by lazy { guildOf(component) }
    }

    private val GLOBAL = Regex("""^([^\x00-\x7F]+)\s+(\w{1,16})\s+»\s""")
    private val CHANNEL = Regex("""^\W*(GUILD|PARTY)\W+(\w{1,16})\s+»\s""")
    private val DM_INCOMING = Regex("""^\W*(\w{1,16})\s+➔\s+YOU\s+»\s""")
    private val DISCORD_CHAT = Regex("""^\W*\[Discord\]\s+(.+?):\s""")

    private val GUILD_LINE = Regex("""Guild:\s*([^\n]+)""")

    fun parse(component: Component): Parsed? {
        val clean = FormatCodes.strip(component.string)

        GLOBAL.find(clean)?.let {
            return Parsed(Channel.GLOBAL, it.groupValues[2], ChatRank.fromSymbol(it.groupValues[1]), component)
        }
        CHANNEL.find(clean)?.let {
            return Parsed(Channel.valueOf(it.groupValues[1]), it.groupValues[2], null, component)
        }
        DM_INCOMING.find(clean)?.let {
            return Parsed(Channel.DM, it.groupValues[1], null, component)
        }
        DISCORD_CHAT.find(clean)?.let {
            return Parsed(Channel.DISCORD, it.groupValues[1], null, component)
        }
        return null
    }

    private fun findHoverText(component: Component): Component? {
        (component.style.hoverEvent as? HoverEvent.ShowText)?.let { return it.value() }
        for (sibling in component.siblings) findHoverText(sibling)?.let { return it }
        return null
    }

    private fun guildOf(component: Component): String? {
        val hover = findHoverText(component) ?: return null
        val text = FormatCodes.strip(hover.string)
        return GUILD_LINE.find(text)?.groupValues?.get(1)?.trim()
    }
}