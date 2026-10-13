package pet.itpuppy.fluffyisles.features

import net.minecraft.network.chat.Component
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.api.ChatMessage

object GlobalChatMute {
    private val section get() = FluffyIsles.config.chatSection.globalMute

    val muted: Boolean get() = section.globalChatMuted

    fun toggle(): Boolean {
        section.globalChatMuted = !section.globalChatMuted
        FluffyIsles.saveConfig()
        return section.globalChatMuted
    }

    fun shouldBlock(message: Component): Boolean {
        if (!muted) return false
        val parsed = ChatMessage.parse(message) ?: return false
        if (parsed.channel == ChatMessage.Channel.DISCORD) return true
        if (parsed.channel != ChatMessage.Channel.GLOBAL) return false
        if (section.allowedPlayers.any { it.equals(parsed.sender, ignoreCase = true) }) return false
        val rank = parsed.rank
        return rank == null || rank.commandName !in section.allowedChatRanks
    }
}