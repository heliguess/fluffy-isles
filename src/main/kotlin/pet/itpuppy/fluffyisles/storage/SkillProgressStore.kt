package pet.itpuppy.fluffyisles.storage

import pet.itpuppy.fluffyisles.FluffyIsles

object SkillProgressStore {
    private const val SAVE_INTERVAL_MS = 30_000L

    private var dirty = false
    private var lastSavedAt = 0L

    private fun accountKey() = FluffyIsles.client.user.profileId.toString()

    private fun entries(): MutableMap<String, StoredProgress> =
        FluffyIsles.config.skillProgress.getOrPut(accountKey()) { mutableMapOf() }

    fun get(skill: String): StoredProgress? = entries()[skill]

    fun put(skill: String, progress: StoredProgress) {
        entries()[skill] = progress
        dirty = true
    }

    fun hasAny(): Boolean =
        FluffyIsles.config.skillProgress[accountKey()]?.isNotEmpty() == true

    fun clear() {
        entries().clear()
        dirty = true
        flush(force = true)
    }

    fun flush(now: Long = System.currentTimeMillis(), force: Boolean = false) {
        if (!dirty || (!force && now - lastSavedAt < SAVE_INTERVAL_MS)) return

        dirty = false
        lastSavedAt = now
        FluffyIsles.config.save()
    }
}