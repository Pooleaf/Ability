package net.pooleaf.ability.ability

import net.pooleaf.ability.player.AbilityPlayer

open class Ability(): Cloneable {

    // 플러그인 이름
    lateinit var pluginName: String
        protected set

    // 이름
    lateinit var name: String
        protected set

    // 플러그인 이름:이름
    val fullName
        get() = "${pluginName}:${name}"

    // 등급
    lateinit var rank: AbilityRank
        protected set

    // 패시브/액티브 타입
    lateinit var type: AbilityType
        protected set

    // 사용법
    lateinit var description: ArrayList<String>
        protected set

    // 할당 받은 플레이어
    var player: AbilityPlayer? = null

    // 능력 밴 여부
    open var ban: Boolean = false


    /**
     * 능력 할당 시 호출됩니다.
     */
    protected open fun onAssign() {}

    /**
     * 능력 할당 해제 시 호출됩니다.
     */
    protected open fun onResign() {}

    /**
     * [player]에게 [Ability]를 할당합니다.
     */
    fun assign(player: AbilityPlayer) {
        // 이미 플레이어가 존재할 경우 할당 해제
        if (this.player != null) {
            resign()
        }

        onAssign()

        this.player = player
    }

    /**
     * [player]에게서 [Ability] 할당을 해제합니다.
     */
    fun resign() {
        if (this.player == null) error("AbilityPlayer for resign ability '${fullName}' is null")

        onResign()

        // 쿨타임 종료
        if (this is Cooldownable) {
            cooldownTimer.cancel()
        }

        // 지속시간 종료
        if (this is Durationable) {
            durationTimer.cancel()
        }

        this.player = null
    }

}