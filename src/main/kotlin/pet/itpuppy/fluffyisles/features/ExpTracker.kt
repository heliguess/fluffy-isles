package pet.itpuppy.fluffyisles.features

import pet.itpuppy.fluffyisles.FluffyIsles
import pet.itpuppy.fluffyisles.api.SkillsAPI
import pet.itpuppy.fluffyisles.storage.SkillProgressStore
import pet.itpuppy.fluffyisles.storage.StoredProgress
import kotlin.math.min

object ExpTracker {
    data class Progress(val level: Int, val current: Long, val required: Long)

    data class Line(
        val skill: String,
        val perHour: Long?,
        val total: Long,
        val activeMs: Long,
        val lastGain: Long,
        val progress: Progress?,
        val etaMs: Long?,
        val paused: Boolean = false,
    )

    private const val MS_PER_HOUR = 3_600_000.0
    private const val MIN_ACTIVE_MS = 10_000L

    private class SkillTracking(val firstAmount: Long, now: Long) {
        var total = firstAmount
        var lastGain = firstAmount
        var activeMs = 0L
        var lastGainAt = now
    }

    private val skills = LinkedHashMap<String, SkillTracking>()

    private var lastInWorldAt = 0L
    private var wasInWorld = false

    private val section get() = FluffyIsles.config.expTrackerSection
    private val idleTimeoutMs get() = section.idleTimeoutSeconds.coerceAtLeast(10) * 1000L
    private val hideAfterMs get() = maxOf(section.hideAfterSeconds, section.idleTimeoutSeconds).coerceAtLeast(10) * 1000L

    fun record(skill: String, amount: Long, now: Long = System.currentTimeMillis()) {
        val key = key(skill)
        track(key, amount, now)
        advanceProgress(key, amount)
    }

    fun setProgress(skill: String, level: Int, current: Long, required: Long) {
        storeProgress(key(skill), Progress(level, current, required))
    }

    fun reset() {
        skills.clear()
    }

    fun reset(skill: String) {
        skills.remove(key(skill))
    }

    fun tick(inWorld: Boolean, now: Long = System.currentTimeMillis()) {
        if (inWorld) {
            if (!wasInWorld && lastInWorldAt != 0L && now - lastInWorldAt > idleTimeoutMs) reset()
            lastInWorldAt = now
        }
        wasInWorld = inWorld
    }

    fun lines(now: Long = System.currentTimeMillis()): List<Line> =
        skills.entries
            .filter { entry ->
                now - entry.value.lastGainAt <= hideAfterMs &&
                        section.hiddenSkills.none { it.equals(entry.key, ignoreCase = true) }
            }
            .sortedByDescending { it.value.total }
            .map { (skill, tracking) ->
                val perHour = perHour(tracking, now)
                val known = progressOf(skill)
                val eta = if (known != null && perHour != null && perHour > 0) {
                    ((known.required - known.current) * MS_PER_HOUR / perHour).toLong()
                } else {
                    null
                }
                Line(
                    skill, perHour, tracking.total, activeMs(tracking, now), tracking.lastGain, known, eta,
                    now - tracking.lastGainAt >= idleTimeoutMs,
                )
            }

    private fun key(skill: String) = skill.trim().uppercase()

    private fun track(key: String, amount: Long, now: Long) {
        val tracking = skills[key]
        if (tracking == null) {
            skills[key] = SkillTracking(amount, now)
            return
        }

        tracking.activeMs += min(now - tracking.lastGainAt, idleTimeoutMs)
        tracking.lastGainAt = now
        tracking.total += amount
        tracking.lastGain = amount
    }

    private fun advanceProgress(key: String, amount: Long) {
        val known = progressOf(key) ?: return

        var level = known.level
        var current = known.current + amount
        var required = known.required
        while (current >= required) {
            current -= required
            level++
            required = SkillsAPI.required(level)
        }
        storeProgress(key, Progress(level, current, required))
    }

    private fun progressOf(key: String): Progress? =
        SkillProgressStore.get(key)?.let { Progress(it.level, it.current, it.required) }

    private fun storeProgress(key: String, progress: Progress) =
        SkillProgressStore.put(key, StoredProgress(progress.level, progress.current, progress.required))

    private fun activeMs(tracking: SkillTracking, now: Long): Long =
        tracking.activeMs + min(now - tracking.lastGainAt, idleTimeoutMs)

    private fun perHour(tracking: SkillTracking, now: Long): Long? {
        val gained = tracking.total - tracking.firstAmount
        val active = activeMs(tracking, now)
        if (gained <= 0 || active < MIN_ACTIVE_MS) return null
        return (gained * MS_PER_HOUR / active).toLong()
    }
}