package pet.itpuppy.fluffyisles.events

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import pet.itpuppy.fluffyisles.api.BossbarAPI
import pet.itpuppy.fluffyisles.api.SkillsAPI
import pet.itpuppy.fluffyisles.features.ExpTracker
import pet.itpuppy.fluffyisles.storage.SkillProgressStore

object ExpTrackerEvent : ClientEvent {
    override fun register() {
        BossbarAPI.onGain { gain ->
            if (gain is BossbarAPI.Gain.Exp) ExpTracker.record(gain.skill, gain.amount)
        }

        SkillsAPI.onUpdate { skills ->
            skills.forEach { ExpTracker.setProgress(it.name, it.level, it.current, it.required) }
            SkillProgressStore.flush(force = true)
        }

        ClientTickEvents.END_CLIENT_TICK.register { minecraft ->
            ExpTracker.tick(minecraft.level != null && minecraft.player != null)
            SkillProgressStore.flush()
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            SkillProgressStore.flush(force = true)
        }
    }
}