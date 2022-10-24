package net.pooleaf.ability.player

import net.pooleaf.gamecore.player.GamePlayer
import java.util.*

class AbilityPlayer(uuid: UUID) : GamePlayer(uuid) {

    var abilityDrew: Boolean = false // 능력 추첨 여부
    var redrawCount: Int = 0 // 능력을 재추첨한 횟수


    override fun init() {
        super.init()

        abilityDrew = false
        redrawCount = 0
    }

}