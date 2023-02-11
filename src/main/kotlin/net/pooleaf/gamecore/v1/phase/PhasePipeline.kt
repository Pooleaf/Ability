package net.pooleaf.gamecore.v1.phase

class PhasePipeline {

    var phases: ArrayList<Phase> = ArrayList()
    var cursor: Int = 0


    /**
     * [PhasePipeline]을 초기화시킵니다.
     */
    fun init() {
        cursor = 0

        phases.forEach { it.init() }
    }

    /**
     * [Phase]를 추가합니다.
     */
    fun addPhase(phase: Phase): PhasePipeline {
        phases.add(phase)
        return this
    }

    /**
     * [delaySeconds]초 동안 Delay를 추가합니다.
     */
    fun addDelay(delaySeconds: Int): PhasePipeline {
        phases.add(DelayPhase(delaySeconds))
        return this
    }

    /**
     * 현재 [Phase]를 반환합니다.
     */
    fun getCurrentPhase(): Phase? {
        if (cursor >= phases.size) return null
        return phases.get(cursor)
    }

    /**
     * 다음 [Phase]로 넘기고 해당 [Phase]를 반환합니다.
     * 마지막 [Phase]일 경우 null을 반환합니다.
     */
    fun nextPhase(): Phase? {
        cursor++
        return getCurrentPhase()
    }

    /**
     * [Phase] 개수를 반환합니다.
     */
    fun getCurrentCount() = phases.size

}