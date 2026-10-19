package pet.itpuppy.fluffyisles.collectors

import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.PlayerScoreEntry
import net.minecraft.world.scores.PlayerTeam
import pet.itpuppy.fluffyisles.utils.helpers.ClientUtils

object ScoreboardCollector {
    private var cached: List<MutableComponent> = listOf()

    private fun collect(): List<MutableComponent> {
        val level = ClientUtils.level ?: return listOf()

        val scoreboard = level.scoreboard
        val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return listOf()

        val lines = scoreboard.listPlayerScores(objective)
            .filterNot { it.isHidden }
            .sortedWith(
                compareByDescending<PlayerScoreEntry> { it.value }
                    .thenBy(String.CASE_INSENSITIVE_ORDER) { it.owner }
            )
            .take(15)
            .mapTo(mutableListOf()) {
                val team = scoreboard.getPlayersTeam(it.owner)
                PlayerTeam.formatNameForTeam(team, it.ownerName())
            }

        lines.addFirst(objective.displayName.copy())
        return lines
    }

    fun tick() {
        cached = collect()
    }

    fun getOrNull(index: Int): MutableComponent? {
        return cached.getOrNull(index)
    }

    fun getFull(): List<MutableComponent> {
        return cached
    }
}