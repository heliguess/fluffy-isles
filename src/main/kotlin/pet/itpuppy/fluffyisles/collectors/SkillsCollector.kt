package pet.itpuppy.fluffyisles.collectors

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.api.SkillsAPI

object SkillsCollector {
    private class Parsed(val stack: ItemStack, val skill: SkillsAPI.Skill?)

    private val PROGRESS = Regex("""Progress:\s*([\d.,]+)\s*/\s*([\d.,]+)\s*Exp""")
    private val TOTAL = Regex("""Total Exp:\s*([\d.,]+)""")
    private val DIGIT_GLYPHS = 0xEA50..0xEA59
    private const val MAX_ATTEMPTS = 40

    private val cache = HashMap<Int, Parsed>()

    private var screen: Screen? = null
    private var done = false
    private var attempts = 0

    fun tick() {
        val current = FluffyIsles.client.screen
        if (current !== screen) {
            screen = current
            done = false
            attempts = 0
            cache.clear()
        }
        if (done || attempts >= MAX_ATTEMPTS) return

        val container = current as? AbstractContainerScreen<*> ?: return
        attempts++

        val inventory = FluffyIsles.client.player?.inventory
        val skills = container.menu.slots.mapIndexedNotNull { index, slot ->
            if (slot.container === inventory) null else parsed(index, slot.item)
        }
        if (skills.isEmpty()) return

        done = true
        SkillsAPI.update(skills)
    }

    private fun parsed(index: Int, stack: ItemStack): SkillsAPI.Skill? {
        val hit = cache[index]
        if (hit != null && hit.stack === stack) return hit.skill

        val skill = parseStack(stack)
        cache[index] = Parsed(stack, skill)
        return skill
    }

    private fun parseStack(stack: ItemStack): SkillsAPI.Skill? {
        if (stack.isEmpty) return null

        val name = stack.get(DataComponents.CUSTOM_NAME)?.string?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val lines = stack.get(DataComponents.LORE)?.lines?.map { it.string } ?: return null
        if (lines.isEmpty()) return null

        val progress = lines.firstNotNullOfOrNull { PROGRESS.find(it) } ?: return null
        val current = number(progress.groupValues[1]) ?: return null
        val required = number(progress.groupValues[2]) ?: return null
        val total = lines.firstNotNullOfOrNull { TOTAL.find(it) }?.let { number(it.groupValues[1]) } ?: 0L

        val level = lines.first()
            .filter { it.code in DIGIT_GLYPHS }
            .map { it.code - DIGIT_GLYPHS.first }
            .joinToString("")
            .toIntOrNull() ?: return null

        return SkillsAPI.Skill(name, level, current, required, total)
    }

    private fun number(text: String): Long? = text.replace(",", "").toLongOrNull()
}