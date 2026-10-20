package pet.itpuppy.fluffyisles.features

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.api.ChatMessageAPI
import pet.itpuppy.fluffyisles.utils.Comp
import pet.itpuppy.fluffyisles.utils.FormatCodes

object ChatLevelTag {
    private val section get() = FluffyIsles.config.chatSection.levelTag

    fun modify(message: Component): Component {
        if (!section.isEnabled) return message

        val sender = ChatMessageAPI.parse(message)?.sender ?: return message
        val level = ChatMessageAPI.totalLevelOf(message) ?: return message

        return insert(message, sender, level) ?: message
    }

    private fun insert(component: Component, sender: String, level: ChatMessageAPI.TotalLevel): MutableComponent? {
        val copy = MutableComponent.create(component.contents).withStyle(component.style)
        var inserted = false

        for (sibling in component.siblings) {
            val rebuilt = if (inserted) null else insert(sibling, sender, level)
            if (rebuilt != null) {
                copy.append(rebuilt)
                inserted = true
                continue
            }

            if (!inserted && FormatCodes.strip(sibling.string).trim() == sender) {
                copy.append(tag(sibling, level))
                inserted = true
            }
            copy.append(sibling)
        }

        return if (inserted) copy else null
    }

    private fun tag(name: Component, level: ChatMessageAPI.TotalLevel): MutableComponent {
        val hover = name.style.hoverEvent
        val click = name.style.clickEvent
        val bracket = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withHoverEvent(hover).withClickEvent(click)
        val number = level.style.withHoverEvent(hover).withClickEvent(click)

        return Comp.build(
            Comp.of("[").withStyle(bracket),
            Comp.of(level.value.toString()).withStyle(number),
            Comp.of("] ").withStyle(bracket),
        )
    }
}