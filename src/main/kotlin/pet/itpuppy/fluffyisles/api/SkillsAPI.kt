package pet.itpuppy.fluffyisles.api

import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

object SkillsAPI {
    data class Skill(val name: String, val level: Int, val current: Long, val required: Long, val total: Long)
    enum class SkillType(val displayName: String) {

        SLAYER("Slayer"),
        COMBAT("Combat"),
        //MERCENARY("Mercenary"), (disabled)
        WOODCUTTING("Woodcutting"),
        FARMING("Farming"),
        GATHERING("Gathering"),
        FISHING("Fishing"),
        MINING("Mining"),
        BREWING("Brewing"),
        COOKING("Cooking"),
        RUNECRAFTING("Runecrafting"),
        SMITHING("Smithing"),
        FLETCHING("Fletching"),
        ARTISAN("Artisan"),
        AUGMENTING("Augmenting"),
        AGILITY("Agility"),
        BEASTMASTER("Beastmaster"),
        CHARISMA("Charisma"),
        //CONSTRUCTION("Construction"), (disabled)
        DUNGEONEERING("Dungeoneering");

        val icon: ItemStack by lazy<ItemStack> {
            ItemStack(Items.GHAST_TEAR).also {
                it.set(
                    DataComponents.ITEM_MODEL,
                    Identifier.fromNamespaceAndPath("isles", "ui_item/skills/ui_button_skills_${name.lowercase()}"),
                )
            }
        }

    }

    private val SCALE = 1600.0 * ln(16.0)

    private val skills = LinkedHashMap<String, Skill>()
    private val listeners = mutableListOf<(List<Skill>) -> Unit>()

    val all: List<Skill> get() = skills.values.toList()

    fun get(name: String): Skill? = skills[name.trim().uppercase()]

//    Exp needed to go from level to level+1: floor(1600 * ln(16) * 16^(level / 25 - 1)).
//     Took this from https://skyblockisles.net/wiki/player-systems/skills-main
//     if it doesn't work it's not my fault :plead:
    fun required(level: Int): Long =
        floor(SCALE * 16.0.pow(level / 25.0 - 1)).toLong()

    fun totalBefore(level: Int): Long =
        (0 until level).sumOf { required(it) }

    fun onUpdate(listener: (List<Skill>) -> Unit) {
        listeners.add(listener)
    }

    fun reset() {
        skills.clear()
    }

    internal fun update(parsed: List<Skill>) {
        parsed.forEach { skills[it.name.trim().uppercase()] = it }
        val snapshot = all
        listeners.forEach { it(snapshot) }
    }
}