package pet.itpuppy.fluffyisles.features

import io.github.vyfor.kpresence.ConnectionState
import io.github.vyfor.kpresence.RichClient
import io.github.vyfor.kpresence.rpc.ActivityType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.api.ConnectionAPI
import pet.itpuppy.fluffyisles.utils.Comp

object RichPresence {
    private const val APP_ID: Long = 1560536246768377988L
    private val client = RichClient(APP_ID)

    private var timestamp = 0L
    private var connectAttempted = false

    fun tick() {
        connect()
        update()
    }

    private val connected
        get() = client.connectionState == ConnectionState.SENT_HANDSHAKE

    private fun connect() {
        if (
            connected ||
            connectAttempted ||
            !ConnectionAPI.onIsles ||
            !FluffyIsles.config.richPresenceSection.isEnabled
        ) return

        try {
            client.connect()
        } catch (e: Exception) {
            FluffyIsles.message(
                Comp.of(
                    "Couldn't connect to Discord! If your Discord client is closed, this message can be ignored. Otherwise - report it as a bug in our GitHub repository!"
                )
            )

            FluffyIsles.LOGGER.error(e.message)
            return
        }

        connectAttempted = true
        timestamp = System.currentTimeMillis()
    }

    private fun disconnect() {
        if (!connected) return
        connectAttempted = false
        client.shutdown()
    }

    private fun update() {
        if (!connected) return

        client.update {
            type = ActivityType.GAME
            state = ConnectionAPI.instance ?: "In Game"

            details = when {
                ConnectionAPI.instance?.startsWith("Hub").isTrue() -> "Sitting around in Hub"
                ConnectionAPI.instance?.startsWith("Isles").isTrue() -> "Exploring the Isles"
                ConnectionAPI.instance?.startsWith("Dungeon").isTrue() -> "Conquering a Rift"
                else -> "Playing on server"
            }

            party {
                id = "party_id"
            }

            timestamps {
                start = timestamp
            }

            assets {
                largeImage = "image_big"
                largeText = "RPC by Fluffy Isles"
            }
        }
    }

    // too lazy to make things public and add new client events
    fun register() {
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            disconnect()
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register {
            disconnect()
        }
    }

    private fun Boolean?.isTrue() = this == true
}