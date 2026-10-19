package pet.itpuppy.fluffyisles.config

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.storage.HudPosition
import pet.itpuppy.fluffyisles.storage.StoredProgress
import pet.itpuppy.fluffyisles.config.section.MiscSection
import pet.itpuppy.fluffyisles.config.section.RarityHighlightSection
import pet.itpuppy.fluffyisles.config.section.ChatSection
import pet.itpuppy.fluffyisles.config.section.RichPresenceSection
import pet.itpuppy.fluffyisles.config.section.ExpTrackerSection

@Config(name = FluffyIsles.MOD_ID)
class ModConfig : ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    var rarityHighlightSection = RarityHighlightSection()

    @ConfigEntry.Gui.CollapsibleObject
    var chatSection = ChatSection()

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    var richPresenceSection = RichPresenceSection()

    @ConfigEntry.Gui.CollapsibleObject
    var expTrackerSection = ExpTrackerSection()

    @ConfigEntry.Gui.CollapsibleObject
    var miscSection = MiscSection()

    @ConfigEntry.Gui.Excluded
    var hudPositions: MutableMap<String, HudPosition> = mutableMapOf()

    @ConfigEntry.Gui.Excluded
    var skillProgress: MutableMap<String, MutableMap<String, StoredProgress>> = mutableMapOf()

    fun save() {
        AutoConfig.getConfigHolder(ModConfig::class.java).save()
    }
}