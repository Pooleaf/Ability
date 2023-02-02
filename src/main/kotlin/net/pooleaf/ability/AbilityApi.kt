package net.pooleaf.ability

import net.pooleaf.ability.ability.AbilityManager
import net.pooleaf.ability.ability.AbilityDrawer
import net.pooleaf.ability.game.AbilityGame
import net.pooleaf.ability.player.AbilityPlayerManager

object AbilityApi {

    val game: AbilityGame = AbilityGame()

    val playerManager: AbilityPlayerManager = AbilityPlayerManager()

    val abilityManager: AbilityManager = AbilityManager()

    val abilityDrawer: AbilityDrawer = AbilityDrawer(false)

    val hiddenAbilityDrawer: AbilityDrawer = AbilityDrawer(false, 7, 100L, 100L)

}