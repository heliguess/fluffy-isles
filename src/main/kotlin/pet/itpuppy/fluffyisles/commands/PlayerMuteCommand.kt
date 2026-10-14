package pet.itpuppy.fluffyisles.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.features.PlayerMute
import pet.itpuppy.fluffyisles.utils.Comp
import pet.itpuppy.utils.command.dsl.command

object PlayerMuteCommand : ClientCommand {
    private val mutedPlayers = SuggestionProvider<FabricClientCommandSource> { _, builder ->
        SharedSuggestionProvider.suggest(PlayerMute.mutedPlayers, builder)
    }
    private val mutedGuilds = SuggestionProvider<FabricClientCommandSource> { _, builder ->
        SharedSuggestionProvider.suggest(PlayerMute.mutedGuilds, builder)
    }

    override fun register() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, context ->
            dispatcher.command(FluffyIsles.MOD_ID, context) {
                "chat" {
                    "ignore player"(StringArgumentType.word()) {
                        executes { name ->
                            val added = PlayerMute.mute(name)
                            FluffyIsles.message(Comp.of(if (added) "Ignored $name" else "$name is already ignored"))
                        }
                    }

                    "unignore player"(StringArgumentType.word(), mutedPlayers) {
                        executes { name ->
                            val removed = PlayerMute.unmute(name)
                            FluffyIsles.message(Comp.of(if (removed) "Unignored $name" else "$name was not ignored"))
                        }
                    }

                    "muteGuild guild"(StringArgumentType.greedyString()) {
                        executes { guild ->
                            val added = PlayerMute.muteGuild(guild)
                            FluffyIsles.message(Comp.of(if (added) "Muted guild $guild" else "Guild $guild is already muted"))
                        }
                    }

                    "unmuteGuild guild"(StringArgumentType.greedyString(), mutedGuilds) {
                        executes { guild ->
                            val removed = PlayerMute.unmuteGuild(guild)
                            FluffyIsles.message(Comp.of(if (removed) "Unmuted guild $guild" else "Guild $guild was not muted"))
                        }
                    }
                }
            }
        }
    }
}