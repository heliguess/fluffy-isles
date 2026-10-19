package pet.itpuppy.fluffyisles.hud

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.storage.HudPosition
import pet.itpuppy.fluffyisles.utils.helpers.ClientUtils

object HudManager {
    val widgets: List<HudWidget> by lazy {
        HudWidget::class.sealedSubclasses.mapNotNull { it.objectInstance }
    }

    fun positionOf(widget: HudWidget): HudPosition {
        val saved = FluffyIsles.config.hudPositions[widget.id] ?: HudPosition(widget.defaultX, widget.defaultY)
        val window = ClientUtils.window
        return HudPosition(
            saved.x.coerceIn(0, (window.guiScaledWidth - 20).coerceAtLeast(0)),
            saved.y.coerceIn(0, (window.guiScaledHeight - 10).coerceAtLeast(0)),
        )
    }

    fun move(widget: HudWidget, x: Int, y: Int) {
        FluffyIsles.config.hudPositions[widget.id] = HudPosition(x, y)
    }

    fun save() {
        FluffyIsles.config.save()
    }

    fun resetAll() {
        FluffyIsles.config.hudPositions.clear()
        save()
    }

    fun render(graphics: GuiGraphics) {
        val screen = ClientUtils.screen
        if (screen is HudEditorScreen || screen is InventoryScreen) return

        renderWidgets(graphics)
    }

    fun renderOverlay(graphics: GuiGraphics) {
        renderWidgets(graphics)
    }

    private fun renderWidgets(graphics: GuiGraphics) {
        widgets.filter { it.isEnabled }.forEach { widget ->
            val position = positionOf(widget)
            widget.render(graphics, position.x, position.y, false)
        }
    }

    private fun regionAt(mouseX: Int, mouseY: Int): HudRegion? =
        widgets.filter { it.isEnabled }.firstNotNullOfOrNull { it.regionAt(mouseX, mouseY) }

    fun renderHover(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        val region = regionAt(mouseX, mouseY) ?: return
        val font = ClientUtils.font

        graphics.fill(region.x - 2, region.y - 2, region.x + region.width + 2, region.y + region.height + 2, 0x30FFFFFF)

        val labelWidth = region.label.maxOf { font.width(it) }
        val labelHeight = region.label.size * 10
        val labelX = mouseX + 10
        val labelY = (mouseY - 2 - labelHeight).coerceAtLeast(2)

        graphics.fill(labelX - 2, labelY - 2, labelX + labelWidth + 2, labelY + labelHeight, 0xC0000000.toInt())
        region.label.forEachIndexed { index, line ->
            graphics.drawString(font, line, labelX, labelY + index * 10, -1)
        }
    }

    fun click(mouseX: Int, mouseY: Int, button: Int, shift: Boolean): Boolean {
        if (button != 0 || !shift) return false
        val region = regionAt(mouseX, mouseY) ?: return false
        region.onClick()
        return true
    }
}