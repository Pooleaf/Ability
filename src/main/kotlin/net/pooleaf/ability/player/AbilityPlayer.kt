package net.pooleaf.ability.player

import net.pooleaf.ability.ability.Ability
import net.pooleaf.gamecore.player.GamePlayer
import java.util.*

class AbilityPlayer(uuid: UUID) : GamePlayer(uuid) {

    // 능력
    var ability: Ability? = null
        private set

    // 임시 능력 (능력 추첨 중 사용)
    var tempAbility: Ability? = null

    // 능력 재추첨 횟수
    var redrawCount: Int = 0

    // 능력 재추첨 가능 횟수
    var maxRedrawCount: Int = 0

    // 능력 추첨 완료 여부
    var abilityDrawComplete: Boolean = false


    fun assignAbility(abilityClass: Class<out Ability>) {
        ability = abilityClass.newInstance()
        ability?.assign(this) ?: error("Failed to assign ability '${abilityClass.name}' to player '${name}'")
    }

    fun resignAbility() {
        ability?.resign()
        ability = null
    }

}