package net.pooleaf.ability.ability

import net.pooleaf.ability.ability.timer.DurationTimer

interface Durationable {

    // 지속시간 (ms)
    val durationMillis: Long
        get() = durationTimer.timeMillis

    // 남은 지속시간 (ms)
    val remainingDurationMillis
        get() = durationTimer.remainingTimeMillis ?: 0

    // 지속시간 타이머
    val durationTimer: DurationTimer

}