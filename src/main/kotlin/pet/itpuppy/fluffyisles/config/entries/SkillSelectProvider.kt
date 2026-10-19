package pet.itpuppy.fluffyisles.config.entries

import me.shedaniel.autoconfig.gui.registry.api.GuiProvider
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.minecraft.network.chat.Component
import pet.itpuppy.fluffyisles.api.SkillsAPI
import java.lang.reflect.Field

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class SkillSelect

object SkillSelectProvider : GuiProvider {
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
        val toggles: List<AbstractConfigListEntry<*>> = SkillsAPI.SkillType.entries.map { skill ->
            IconToggleEntry(
                Component.literal(IconToggleEntry.Companion.ICON_PADDING + skill.displayName),
                skill.icon,
                selected.none { it.equals(skill.name, ignoreCase = true) },
            ) { shown ->
                selected.removeIf { it.equals(skill.name, ignoreCase = true) }
                if (!shown) selected.add(skill.name)
            }
        }

        return listOf(
            builder.startSubCategory(Component.translatable(i13n), toggles)
                .setTooltip(Component.translatable("$i13n.@Tooltip"))
                .setExpanded(false)
                .build()
        )
    }
}