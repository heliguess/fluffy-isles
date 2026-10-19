package pet.itpuppy.fluffyisles.config.entries

import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

class IconToggleEntry(
    label: Component,
    private val icon: ItemStack,
    value: Boolean,
    saveConsumer: (Boolean) -> Unit,
) : BooleanListEntry(
    label,
    value,
    Component.translatable("text.cloth-config.reset_value"),
    { true },
    Consumer<Boolean> { saveConsumer(it) },
    null,
    false,
) {
    override fun render(
        graphics: GuiGraphics,
        index: Int,
        y: Int,
        x: Int,
        entryWidth: Int,
        entryHeight: Int,
        mouseX: Int,
        mouseY: Int,
        isHovered: Boolean,
        delta: Float,
    ) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta)
        graphics.renderItem(icon, x, y + 2)
    }

    companion object {
        const val ICON_PADDING = "     "
    }
}