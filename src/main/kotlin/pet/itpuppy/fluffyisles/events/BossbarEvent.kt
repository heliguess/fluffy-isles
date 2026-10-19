package pet.itpuppy.fluffyisles.events

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import pet.itpuppy.fluffyisles.api.BossbarAPI
import pet.itpuppy.fluffyisles.collectors.BossbarCollector

object BossbarEvent : ClientEvent {
    override fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(::run)
    }

    private fun run(minecraft: Minecraft) {
        if (minecraft.level == null || minecraft.player == null) {
            BossbarAPI.reset()
            return
        }

        BossbarCollector.tick()
        BossbarAPI.tick()
    }
}