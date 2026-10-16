package pet.itpuppy.fluffyisles.api

import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.collectors.ScoreboardCollector

object ConnectionAPI {
    var onIsles: Boolean = false; private set
    var instance: String? = null; private set
    var version: String? = null; private set

    private fun updateOnIsles(): Boolean {
        return FluffyIsles.client.connection?.serverData?.ip in setOf(
            "play.skyblockisles.net",
            "skyblockisles.net"
        )
    }

    private fun updateInstance(): String? {
        if (!onIsles) return null
        return ScoreboardCollector.getOrNull(2)?.siblings[0]?.string?.substringBefore(" ")
    }

    private fun updateVersion(): String? {
        if (!onIsles) return null
        return ScoreboardCollector.getOrNull(2)?.siblings[0]?.string?.substringAfterLast(" ")
    }

    fun tick() {
        onIsles = updateOnIsles()
        instance = updateInstance()
        version = updateVersion()
    }
}