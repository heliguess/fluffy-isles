package pet.itpuppy.fluffyisles.events

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import pet.itpuppy.fluffyisles.api.ConnectionAPI
import pet.itpuppy.fluffyisles.collectors.ScoreboardCollector
import pet.itpuppy.fluffyisles.features.RichPresence

object PerSecondEvent : ClientEvent {
    private var tickCounter = 0
    private const val SECOND = 20

    override fun register() {
        ClientTickEvents.END_CLIENT_TICK.register(::run)
    }

    private fun run(minecraft: Minecraft) {
        if (minecraft.level == null || minecraft.player == null) {
            tickCounter = 0
            return
        }

        if (tickCounter++ >= SECOND) {
            collectorCallback()
            apiCallback()
            featureCallback()

            tickCounter = 0
        }
    }
    
    private fun collectorCallback() {
        ScoreboardCollector.tick()
    }

    private fun apiCallback() {
        ConnectionAPI.tick()
    }

    private fun featureCallback() {
        RichPresence.tick()
    }
}