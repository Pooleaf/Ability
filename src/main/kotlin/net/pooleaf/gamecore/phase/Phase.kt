package net.pooleaf.gamecore.phase

abstract class Phase {

    var started: Boolean = false // 시작 여부
    var ended: Boolean = false // 종료 여부

    var count: Int = 0 // 초


    /**
     * [Phase]가 시작될 때 실행됩니다.
     */
    protected open fun onStart() {}

    /**
     * [Phase]가 진행 중일 때 1초마다 실행됩니다.
     * 이 시점의 [count]는 1 이상입니다.
     */
    protected open fun onRun() {}

    /**
     * [Phase]를 종료될 때 실행됩니다.
     */
    protected open fun onEnd() {}

    /**
     * [Phase]가 중단될 때 실행됩니다.
     */
    protected open fun onCancel() {}


    /**
     * [Phase]를 초기화합니다.
     */
    fun init() {
        started = false
        ended = false

        count = 0
    }

    /**
     * [Phase]를 시작시킵니다.
     */
    fun start() {
        if (started) return

        started = true
        count = 0

        onStart()
    }

    /**
     * [Phase]를 진행시킵니다.
     */
    fun run() {
        if (!started) return

        count++

        onRun()
    }

    /**
     * [Phase]를 종료시킵니다.
     */
    fun end() {
        if (!started) return

        ended = true
        count = 0

        onEnd()
    }

    /**
     * [Phase]를 중단시킵니다.
     */
    fun cancel() {
        if (!started) return

        init()

        onCancel()
    }

}