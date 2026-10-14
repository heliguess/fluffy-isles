package pet.itpuppy.fluffyisles.commands

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import pet.itpuppy.fluffyisles.FluffyIsles

object AlternativeRootCommand : ClientCommand {
    override fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            val root = dispatcher.register(
                ClientCommandManager.literal(FluffyIsles.MOD_ID)
            )

            dispatcher.register(
                ClientCommandManager.literal(FluffyIsles.ALT_ID).redirect(root)
            )
        }
    }
}