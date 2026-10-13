package pet.itpuppy.fluffyisles.config.section.chat

import me.shedaniel.autoconfig.annotation.ConfigEntry

class PlayerMuteSubsection {
    var mutedPlayers: MutableList<String> = mutableListOf()

    var mutedGuilds: MutableList<String> = mutableListOf()

    @ConfigEntry.Gui.Tooltip
    var showMutedPlaceholder: Boolean = false
}