package pet.itpuppy.fluffyisles.commands

import me.shedaniel.autoconfig.AutoConfigClient
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.config.ModConfig
import pet.itpuppy.fluffyisles.utils.helpers.ClientUtils
import pet.itpuppy.utils.command.dsl.command

object AlternativeRootCommand : ClientCommand {
    override fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, context ->
            val root = dispatcher.register(
                ClientCommandManager.literal(FluffyIsles.MOD_ID)
            )

            dispatcher.register(
                ClientCommandManager.literal(FluffyIsles.ALT_ID).redirect(root)
            )

            // messy way to get the alt root to open config screen too
            dispatcher.command(FluffyIsles.ALT_ID, context) {
                opens {
                    AutoConfigClient.getConfigScreen(ModConfig::class.java, ClientUtils.screen).get()
                }
            }
        }
    }
}