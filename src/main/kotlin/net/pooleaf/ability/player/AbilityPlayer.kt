package net.pooleaf.ability.player

import kotlinx.coroutines.Job
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.compat.CompatAbility
import net.pooleaf.gamecore.player.GamePlayer
import java.util.*

class AbilityPlayer(uuid: UUID) : GamePlayer(uuid) {

    // 능력
    var ability: Ability? = null
        private set

    // 임시 능력 (능력 추첨 중 사용)
    var tempAbility: Ability? = null
        internal set

    // 능력 할당 예약 (오프라인 중 능력이 할당될 경우)
    var isAbilityAssignReserved: Boolean = false
        internal set

    // 능력 재추첨 횟수
    var redrawCount: Int = 0
        internal set

    // 능력 재추첨 가능 횟수
    var maxRedrawCount: Int = 0
        internal set

    // 능력 추첨 완료 여부
    var abilityDrawComplete: Boolean = false
        internal set

    // 능력 추첨 타이머
    var abilityDrawJob: Job? = null
        internal set


    /**
     * 플레이어에게 능력을 부여합니다.
     */
    fun assignAbility(ability: Ability) {
        if (ability is CompatAbility<*>) {
            this.ability = ability.clone() as Ability
        } else {
            this.ability = ability.javaClass.newInstance()
        }

        // 오프라인일 경우 할당 예약
        if (!isOnline) {
            isAbilityAssignReserved = true
            return
        }

        this.ability?.assign(this) ?: error("Failed to assign ability '${ability.name}' to player '${name}'")
    }

    /**
     * 플레이어의 능력을 제거합니다.
     */
    fun resignAbility() {
        ability?.resign()
        ability = null
    }

}