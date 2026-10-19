package pet.itpuppy.fluffyisles.events

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.hud.HudManager

object HudEvent : ClientEvent {
    override fun register() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, FluffyIsles.id("hud")) { graphics, _ ->
            HudManager.render(graphics)
        }
    }
}