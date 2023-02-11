package net.pooleaf.ability.game

import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.gamecore.game.Game
import net.pooleaf.gamecore.phase.PhasePipeline
import net.pooleaf.gamecore.phases.*
import org.bukkit.GameMode

class AbilityGame: Game(
    100,
    PhasePipeline()
        .addPhase(StartCountPhase(true))
        .addPhase(DelayPhase(2))
        .addPhase(AbilityDrawPhase())
        .addPhase(RunnablePhase() {
            BukkitSyncScope.launch { AbilityApi.game.changeGameMode(GameMode.SURVIVAL) }
            AbilityApi.game.isPvpStarted = true
        })
        .addPhase(GamePhase())
        .addPhase(EndPhase())
) {

    var abilityDrawStarted: Boolean = false

}