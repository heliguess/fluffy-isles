package pet.itpuppy.fluffyisles.hud

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

data class HudSize(val width: Int, val height: Int)

data class HudRegion(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    val label: List<Component>,
    val onClick: () -> Unit,
)

sealed interface HudWidget {
    val id: String
    val displayName: String
    val defaultX: Int
    val defaultY: Int
    val isEnabled: Boolean

    fun render(graphics: GuiGraphics, x: Int, y: Int, editing: Boolean): HudSize

    fun regionAt(mouseX: Int, mouseY: Int): HudRegion? = null
}

