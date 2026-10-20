package pet.itpuppy.fluffyisles.config.section

import me.shedaniel.autoconfig.annotation.ConfigEntry
import pet.itpuppy.fluffyisles.config.section.chat.GlobalMuteSubsection
import pet.itpuppy.fluffyisles.config.section.chat.LevelTagSubsection
import pet.itpuppy.fluffyisles.config.section.chat.PlayerMuteSubsection

class ChatSection {
    @ConfigEntry.Gui.CollapsibleObject
    var globalMute = GlobalMuteSubsection()

    @ConfigEntry.Gui.CollapsibleObject
    var playerMute = PlayerMuteSubsection()

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    var levelTag = LevelTagSubsection()
}