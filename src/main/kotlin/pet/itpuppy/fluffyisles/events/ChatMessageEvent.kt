package pet.itpuppy.fluffyisles.events

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Component
import pet.itpuppy.fluffyisles.features.GlobalChatMute
import pet.itpuppy.fluffyisles.features.PlayerMute

object ChatMessageEvent : ClientEvent {
    override fun register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            overlay || allow(message)
        }

        ClientReceiveMessageEvents.ALLOW_CHAT.register { message, _, _, _, _ ->
            allow(message)
        }
    }
    
    private fun allow(message: Component): Boolean {
        return !GlobalChatMute.shouldBlock(message) && PlayerMute.handle(message)
    }
}