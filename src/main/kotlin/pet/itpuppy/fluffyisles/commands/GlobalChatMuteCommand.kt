package pet.itpuppy.fluffyisles.commands

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.features.GlobalChatMute
import pet.itpuppy.fluffyisles.utils.Comp
import pet.itpuppy.utils.command.dsl.command

object GlobalMuteCommand : ClientCommand{
    override fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, context ->
            dispatcher.command(FluffyIsles.MOD_ID, context) {
                "chat globalMute" executes {
                    val muted = GlobalChatMute.toggle()
                    FluffyIsles.message(Comp.of("Global chat ${if (muted) "muted" else "unmuted"}"))
                }
            }
        }
    }
}