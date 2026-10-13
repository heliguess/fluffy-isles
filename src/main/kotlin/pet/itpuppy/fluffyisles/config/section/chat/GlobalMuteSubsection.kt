package pet.itpuppy.fluffyisles.config.section.chat

import pet.itpuppy.fluffyisles.config.entries.RankSelect

class GlobalMuteSubsection {
    var globalChatMuted: Boolean = false

    var allowedPlayers: MutableList<String> = mutableListOf()

    @RankSelect
    var allowedChatRanks: MutableList<String> = mutableListOf()

}