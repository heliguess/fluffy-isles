package pet.itpuppy.fluffyisles.events

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import pet.itpuppy.fluffyisles.collectors.SkillsCollector

object SkillsEvent : ClientEvent {
    override fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(::run)
    }

    private fun run(minecraft: Minecraft) {
        if (minecraft.level == null || minecraft.player == null) return

        SkillsCollector.tick()
    }
}