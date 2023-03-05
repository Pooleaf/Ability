package net.pooleaf.ability.ability

import net.pooleaf.ability.ability.timer.CoolDownTimer

interface Cooldownable {

    // 쿨타임 (ms)
    val cooldownMillis
        get() = cooldownTimer.timeMillis

    // 남은 쿨타임 (ms)
    val remainingCooldownMillis
        get() = cooldownTimer.remainingTimeMillis ?: 0

    // 쿨타임 타이머
    val cooldownTimer: CoolDownTimer

}