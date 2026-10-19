package pet.itpuppy.fluffyisles.hud

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import pet.itpuppy.fluffyisles.storage.HudPosition
import pet.itpuppy.fluffyisles.utils.Comp

class HudEditorScreen : Screen(Component.literal("HUD Editor")) {
    private val sizes = HashMap<HudWidget, HudSize>()
    private var dragging: HudWidget? = null
    private var grabX = 0
    private var grabY = 0

    override fun init() {
        addRenderableWidget(
            Button.builder(Component.literal("Reset all")) { HudManager.resetAll() }
                .bounds(width / 2 - 105, height - 30, 100, 20)
                .build()
        )
        addRenderableWidget(
            Button.builder(Component.literal("Done")) { onClose() }
                .bounds(width / 2 + 5, height - 30, 100, 20)
                .build()
        )
    }

    override fun isPauseScreen(): Boolean = false

    override fun removed() {
        HudManager.save()
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (super.mouseClicked(event, doubleClick)) return true
        if (event.button() != 0) return false

        val mouseX = event.x().toInt()
        val mouseY = event.y().toInt()
        val target = HudManager.widgets.lastOrNull { widget ->
            widget.isEnabled && sizes[widget]?.let { contains(HudManager.positionOf(widget), it, mouseX, mouseY) } == true
        } ?: return false

        val position = HudManager.positionOf(target)
        dragging = target
        grabX = mouseX - position.x
        grabY = mouseY - position.y
        return true
    }

    override fun mouseDragged(event: MouseButtonEvent, dragX: Double, dragY: Double): Boolean {
        val target = dragging ?: return super.mouseDragged(event, dragX, dragY)
        val size = sizes[target] ?: return true

        HudManager.move(
            target,
            (event.x().toInt() - grabX).coerceIn(0, (width - size.width).coerceAtLeast(0)),
            (event.y().toInt() - grabY).coerceIn(0, (height - size.height).coerceAtLeast(0)),
        )
        return true
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        if (dragging == null) return super.mouseReleased(event)

        dragging = null
        HudManager.save()
        return true
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(graphics, mouseX, mouseY, delta)

        HudManager.widgets.filter { it.isEnabled }.forEach { widget ->
            val position = HudManager.positionOf(widget)
            val size = widget.render(graphics, position.x, position.y, true)
            sizes[widget] = size

            val active = widget === dragging || (dragging == null && contains(position, size, mouseX, mouseY))
            outline(graphics, position.x - 2, position.y - 2, size.width + 4, size.height + 4, if (active) HIGHLIGHT else FRAME)
            graphics.drawString(font, Comp.of(widget.displayName, ChatFormatting.GRAY), position.x - 2, position.y - 12, -1)
        }

        graphics.drawCenteredString(font, Component.literal("Drag elements to move them"), width / 2, 10, -1)
    }

    private fun contains(position: HudPosition, size: HudSize, mouseX: Int, mouseY: Int): Boolean =
        mouseX in position.x until position.x + size.width && mouseY in position.y until position.y + size.height

    private fun outline(graphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int, color: Int) {
        graphics.fill(x, y, x + width, y + 1, color)
        graphics.fill(x, y + height - 1, x + width, y + height, color)
        graphics.fill(x, y, x + 1, y + height, color)
        graphics.fill(x + width - 1, y, x + width, y + height, color)
    }

    private companion object {
        val FRAME = 0x80FFFFFF.toInt()
        val HIGHLIGHT = 0xFF55FFFF.toInt()
    }
}