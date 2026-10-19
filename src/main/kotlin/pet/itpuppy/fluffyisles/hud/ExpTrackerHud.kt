package pet.itpuppy.fluffyisles.hud

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.api.SkillsAPI
import pet.itpuppy.fluffyisles.features.ExpTracker
import pet.itpuppy.fluffyisles.storage.SkillProgressStore
import pet.itpuppy.fluffyisles.utils.Comp
import pet.itpuppy.fluffyisles.utils.TextUtils.duration
import pet.itpuppy.fluffyisles.utils.TextUtils.gradient
import pet.itpuppy.fluffyisles.utils.TextUtils.toFormattedNumber
import pet.itpuppy.fluffyisles.utils.TextUtils.toTitleCase

object ExpTrackerHud : HudWidget {
    private const val LINE_HEIGHT = 10
    private const val BLOCK_GAP = 4

    private class Block(val skill: String, val x: Int, val y: Int, val width: Int, val height: Int)

    private var blocks: List<Block> = emptyList()

    private val SAMPLE = listOf(
        ExpTracker.Line(
            "Woodcutting", 12_300, 1_400, 750_000, 35,
            ExpTracker.Progress(23, 2_800, SkillsAPI.required(23)), 3_900_000,
        ),
        ExpTracker.Line("Agility", null, 500, 0, 5, null, null),
    )

    override val id = "exp_tracker"
    override val displayName = "Exp Tracker"
    override val defaultX = 5
    override val defaultY = 5
    override val isEnabled: Boolean get() = FluffyIsles.config.expTrackerSection.isEnabled

    private val FOOTER = Comp.of("Run /skills to sync your skill data", ChatFormatting.YELLOW)

    override fun render(graphics: GuiGraphics, x: Int, y: Int, editing: Boolean): HudSize {
        if (!editing) blocks = emptyList()

        var lines = ExpTracker.lines()
        if (lines.isEmpty() && editing) lines = SAMPLE

        val needsSync = !SkillProgressStore.hasAny() || lines.any { it.progress == null }
        if (lines.isEmpty() && !needsSync) return HudSize(0, 0)

        val font = FluffyIsles.client.font
        val found = mutableListOf<Block>()
        var width = 0
        var currentY = y

        lines.forEach { line ->
            val rows = listOf(header(line), rate(line), eta(line))
            rows.forEachIndexed { index, row ->
                graphics.drawString(font, row, x, currentY + index * LINE_HEIGHT, -1)
            }

            val blockWidth = rows.maxOf { font.width(it) }
            width = maxOf(width, blockWidth)
            found.add(Block(line.skill, x, currentY, blockWidth, rows.size * LINE_HEIGHT))
            currentY += rows.size * LINE_HEIGHT + BLOCK_GAP
        }

        if (needsSync) {
            graphics.drawString(font, FOOTER, x, currentY, -1)
            width = maxOf(width, font.width(FOOTER))
            currentY += LINE_HEIGHT + BLOCK_GAP
        }

        if (!editing) blocks = found
        return HudSize(width, currentY - y - BLOCK_GAP)
    }

    override fun regionAt(mouseX: Int, mouseY: Int): HudRegion? {
        val block = blocks.firstOrNull {
            mouseX in it.x until it.x + it.width && mouseY in it.y until it.y + it.height
        } ?: return null

        return HudRegion(
            block.x, block.y, block.width, block.height,
            listOf(
                Comp.of("Shift-click to reset ${block.skill.toTitleCase()}", ChatFormatting.YELLOW),
                Comp.of("To move the widget run /fi hud", ChatFormatting.GRAY),
            ),
        ) { ExpTracker.reset(block.skill) }
    }

    private fun header(line: ExpTracker.Line): Component {
        val parts = mutableListOf<Component>(
            Comp.of(line.skill.toTitleCase(), ChatFormatting.GOLD).withStyle { it.withBold(true) }
        )
        line.progress?.let { progress ->
            val tenths = if (progress.required > 0) progress.current * 1000 / progress.required else 0
            parts.add(Comp.of(" Lv.${progress.level}", ChatFormatting.GRAY))
            parts.add(Comp.of(" (${tenths / 10}.${tenths % 10}%)", ChatFormatting.AQUA))
            parts.add(Comp.of(" "))
            parts.add(
                gradient(
                    "${progress.current.toFormattedNumber()}/${progress.required.toFormattedNumber()}",
                    boldLength = progress.current.toFormattedNumber().length,
                )
            )
        }
        parts.add(Comp.of(" (+${line.lastGain.toFormattedNumber()})", ChatFormatting.DARK_GRAY))
        return Comp.build(*parts.toTypedArray())
    }

    private fun rate(line: ExpTracker.Line): Component {
        val parts = mutableListOf<Component>(
            Comp.of("Exp/hr: ", ChatFormatting.GRAY),
            gradient(line.perHour?.toFormattedNumber() ?: "--"),
            Comp.of("    Total exp: ", ChatFormatting.GRAY),
            gradient(line.total.toFormattedNumber()),
            Comp.of(" in ${duration(line.activeMs)}", ChatFormatting.GRAY),
        )
        if (line.paused) parts.add(Comp.of(" (Paused)", ChatFormatting.YELLOW))
        return Comp.build(*parts.toTypedArray())
    }

    private fun eta(line: ExpTracker.Line): Component = Comp.build(
        Comp.of("Time until next level: ", ChatFormatting.GRAY),
        Comp.of(line.etaMs?.let { duration(it) } ?: "--", ChatFormatting.AQUA),
    )
}