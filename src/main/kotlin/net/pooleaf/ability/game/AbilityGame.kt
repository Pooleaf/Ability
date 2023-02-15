package net.pooleaf.ability.game

import net.pooleaf.gamecore.game.Game

class AbilityGame: Game(
    100,
    AbilityPhasePipeline()
) {

    var abilityDrawStarted: Boolean = false

}