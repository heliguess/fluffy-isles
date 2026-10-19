package pet.itpuppy.fluffyisles.events

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import pet.itpuppy.fluffyisles.hud.HudManager

object HudInteractionEvent : ClientEvent {
    override fun register() {
        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
            if (screen !is InventoryScreen) return@register

            ScreenEvents.afterRender(screen).register { _, graphics, mouseX, mouseY, _ ->
                HudManager.renderOverlay(graphics)
                HudManager.renderHover(graphics, mouseX, mouseY)
            }

            ScreenMouseEvents.allowMouseClick(screen).register { _, click ->
                !HudManager.click(click.x().toInt(), click.y().toInt(), click.button(), click.hasShiftDown())
            }
        }
    }
}