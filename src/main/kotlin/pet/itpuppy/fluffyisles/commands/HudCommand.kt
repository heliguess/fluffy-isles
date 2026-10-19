package pet.itpuppy.fluffyisles.commands

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.hud.HudEditorScreen
import pet.itpuppy.utils.command.dsl.command

object HudCommand : ClientCommand {
    override fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, context ->
            dispatcher.command(FluffyIsles.MOD_ID, context) {
                "hud" {
                    opens { HudEditorScreen() }
                }
            }
        }
    }
}