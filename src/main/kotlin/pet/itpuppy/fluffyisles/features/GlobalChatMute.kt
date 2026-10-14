package pet.itpuppy.fluffyisles.features

import net.minecraft.network.chat.Component
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.api.ChatMessageAPI

object GlobalChatMute {
    private val section get() = FluffyIsles.config.chatSection.globalMute

    val muted: Boolean get() = section.globalChatMuted

    fun toggle(): Boolean {
        section.globalChatMuted = !section.globalChatMuted

        FluffyIsles.config.save()
        return section.globalChatMuted
    }

    fun shouldBlock(message: Component): Boolean {
        if (!muted) return false

        val parsed = ChatMessageAPI.parse(message) ?: return false
        if (parsed.channel == ChatMessageAPI.Channel.DISCORD) return true
        if (parsed.channel != ChatMessageAPI.Channel.GLOBAL) return false

        if (section.allowedPlayers.any { it.equals(parsed.sender, ignoreCase = true) }) return false

        val rank = parsed.rank
        return rank == null || rank.commandName !in section.allowedChatRanks
    }
}