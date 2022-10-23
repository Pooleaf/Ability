package net.pooleaf.ability

import net.pooleaf.ability.game.AbilityGame
import net.pooleaf.ability.player.AbilityPlayerManager

class AbilityApi {

    companion object {

        val game: AbilityGame = AbilityGame()

        val playerManager: AbilityPlayerManager = AbilityPlayerManager()

    }

}