package net.pooleaf.ability.ability

import net.pooleaf.ability.AbilityApi

class AbilityService {

    fun canUseAbility(): Boolean {
        return AbilityApi.game.isGameStarted && !AbilityApi.game.isEnded && !AbilityApi.game.isGodMode
    }

}