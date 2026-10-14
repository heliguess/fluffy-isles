package pet.itpuppy.fluffyisles.features

import net.minecraft.network.chat.Component
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.api.ChatMessageAPI
import net.minecraft.ChatFormatting
import pet.itpuppy.fluffyisles.utils.Comp
import pet.itpuppy.fluffyisles.utils.Comp.withHoverText

object PlayerMute {
    private val section get() = FluffyIsles.config.chatSection.playerMute

    val mutedPlayers: List<String> get() = section.mutedPlayers
    val mutedGuilds: List<String> get() = section.mutedGuilds

    fun handle(message: Component): Boolean {
        if (!shouldBlock(message)) return true
        if (section.showMutedPlaceholder) showPlaceholder(message)
        return false
    }

    private fun showPlaceholder(message: Component) {
        FluffyIsles.client.gui.chat.addMessage(
            Comp.build(
                Comp.of("["),
                Comp.of("✖", ChatFormatting.RED),
                Comp.of("]"),
                Comp.of(" One message from a person you muted ").withStyle { it.withItalic(true) },
            ).withStyle(ChatFormatting.GRAY).withHoverText(message)
        )
    }

    fun mute(name: String): Boolean {
        if (section.mutedPlayers.any { it.equals(name, ignoreCase = true) }) return false
        section.mutedPlayers.add(name.lowercase())
        FluffyIsles.saveConfig()
        return true
    }

    fun unmute(name: String): Boolean {
        val removed = section.mutedPlayers.removeIf { it.equals(name, ignoreCase = true) }
        if (removed) FluffyIsles.saveConfig()
        return removed
    }

    fun muteGuild(name: String): Boolean {
        if (section.mutedGuilds.any { it.equals(name, ignoreCase = true) }) return false
        section.mutedGuilds.add(name.lowercase())
        FluffyIsles.saveConfig()
        return true
    }

    fun unmuteGuild(name: String): Boolean {
        val removed = section.mutedGuilds.removeIf { it.equals(name, ignoreCase = true) }
        if (removed) FluffyIsles.saveConfig()
        return removed
    }

    private fun shouldBlock(message: Component): Boolean {
        if (section.mutedPlayers.isEmpty() && section.mutedGuilds.isEmpty()) return false
        val parsed = ChatMessageAPI.parse(message) ?: return false
        if (section.mutedPlayers.any { it.equals(parsed.sender, ignoreCase = true) }) return true
        if (section.mutedGuilds.isEmpty()) return false
        val guild = ChatMessageAPI.guildOf(message) ?: return false
        return section.mutedGuilds.any { it.equals(guild, ignoreCase = true) }
    }
}