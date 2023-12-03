package net.pooleaf.ability.ability.timer

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope

/**
 * [timeMillis] 진행할 시간 (ms)
 * [intervalMillis] 실행 간격 (ms)
 */
open class SimpleTimer(
    val timeMillis: Long,
    val intervalMillis: Long
) {

    // 타이머 진행 여부
    val isRunning: Boolean
        get() = runJob?.isActive == true && endJob?.isActive == true

    // 시작 시간
    var startTimeMillis: Long? = null

    // 진행 시간 (ms)
    val runningTimeMillis: Long?
        get() = startTimeMillis?.let { System.currentTimeMillis() - it }

    // 남은 시간 (ms)
    val remainingTimeMillis: Long?
        get() = runningTimeMillis?.let { timeMillis - it }


    /**
     * [run] 메소드를 실행시키는 [Job]
     */
    private var runJob: Job? = null

    /**
     * [end] 메소드를 실행시키는 [Job]
     */
    private var endJob: Job? = null


    /**
     * 타이머 시작 시 호출됩니다.
     */
    open fun onStart() {}

    /**
     * 타이머 정상 종료 시 호출됩니다.
     */
    open fun onEnd() {}

    /**
     * 타이머 진행 중 [intervalMillis]마다 호출됩니다.
     * 시작할 때와 종료될 때는 호출되지 않습니다.
     */
    open fun onRun() {}

    /**
     * 타이머 중단 시 호출됩니다.
     */
    open fun onCancel() {}


    /**
     * 타이머를 시작합니다.
     */
    fun start() {
        if (isRunning) error("Timer already started")

        onStart()

        startTimeMillis = System.currentTimeMillis()

        runJob = BukkitAsyncScope.launch {
            while (remainingTimeMillis?.let { 0 < it } == true) {
                delay(intervalMillis)
                run()
            }
        }
        endJob = BukkitAsyncScope.launch {
            delay(timeMillis)
            end()
        }
    }

    /**
     * 타이머를 작동 시킵니다.
     */
    private fun run() {
        if (!isRunning) error("Timer not started")

        onRun()
    }

    /**
     * 타이머를 정상 종료시킵니다.
     */
    private fun end() {
        if (!isRunning) error("Timer not started")

        onEnd()

        startTimeMillis = null

        runJob?.cancel()
        endJob?.cancel()
    }

    /**
     * 타이머를 중단시킵니다.
     */
    fun cancel() {
        if (!isRunning) error("Timer not started")

        onCancel()

        startTimeMillis = null

        runJob?.cancel()
        endJob?.cancel()
    }

}