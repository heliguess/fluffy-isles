package pet.itpuppy.fluffyisles.config.entries

import me.shedaniel.autoconfig.gui.registry.api.GuiProvider
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.minecraft.network.chat.Component
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.api.ChatRank
import java.lang.reflect.Field

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class RankSelect

object RankSelectProvider : GuiProvider {
    override fun get(
        i13n: String,
        field: Field,
        config: Any,
        defaults: Any,
        registry: GuiRegistryAccess,
    ): List<AbstractConfigListEntry<*>> {
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val selected = field.get(config) as MutableList<String>

        val builder = ConfigEntryBuilder.create()
        val toggles = ChatRank.entries.map { rank ->
            builder.startBooleanToggle(Component.literal("${rank.symbol} ${rank.displayName}"), rank.commandName in selected)
                .setDefaultValue(false)
                .setSaveConsumer { enabled ->
                    selected.remove(rank.commandName)
                    if (enabled) selected.add(rank.commandName)
                }
                .build()
        }

        return listOf(
            builder.startSubCategory(
                Component.translatable("text.autoconfig.${FluffyIsles.MOD_ID}.option.chatSection.globalMute.allowedChatRanks"),
                toggles
            )
                .setTooltip(Component.translatable("text.autoconfig.${FluffyIsles.MOD_ID}.option.chatSection.globalMute.allowedChatRanks.@Tooltip"))
                .setExpanded(false)
                .build()
        )
    }
}