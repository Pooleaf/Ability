package net.pooleaf.ability.player

import net.pooleaf.ability.ability.Ability
import net.pooleaf.gamecore.player.GamePlayer
import java.util.*

class AbilityPlayer(uuid: UUID) : GamePlayer(uuid) {

    /**
     * 할당 받은 능력
     */
    var ability: Ability? = null

    /**
     * 능력 추첨 완료 여부
     */
    var abilityDrawComplete: Boolean = false

    /**
     * 능력 재추첨 횟수
     */
    var redrawCount: Int = 0


    override suspend fun init() {
        super.init()

        ability
        abilityDrawComplete = false
        redrawCount = 0
    }

    fun assignAbility(abilityClass: Class<out Ability>) {
        ability = abilityClass.newInstance()
        ability?.assign(this) ?: error("Failed to assign ability '${abilityClass.name}' to player '${name}'")
    }

    fun resignAbility() {
        ability?.resign()
        ability = null
    }

}