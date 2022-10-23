package net.pooleaf.ability

import net.pooleaf.ability.game.AbilityGame
import net.pooleaf.ability.player.AbilityPlayerManager

object AbilityApi {

    val game: AbilityGame = AbilityGame()

    val playerManager: AbilityPlayerManager = AbilityPlayerManager()

}