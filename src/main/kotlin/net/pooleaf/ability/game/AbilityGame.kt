package net.pooleaf.ability.game

import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.game.Game
import net.pooleaf.gamecore.phase.PhasePipeline
import net.pooleaf.gamecore.phases.*
import org.bukkit.GameMode

class AbilityGame: Game(
    100,
    AbilityPhasePipeline()
) {

    var abilityDrawStarted: Boolean = false

}