package net.pooleaf.ability.game

import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.game.Game
import net.pooleaf.gamecore.phase.DelayPhase
import net.pooleaf.gamecore.phase.PhasePipeline
import net.pooleaf.gamecore.phase.RunnablePhase
import net.pooleaf.gamecore.phases.EndPhase
import net.pooleaf.gamecore.phases.GamePhase
import net.pooleaf.gamecore.phases.StartCountPhase
import org.bukkit.GameMode

class AbilityGame: Game(100) {

    var abilityDrawStarted: Boolean = false


    override fun createPhasePipeline(): PhasePipeline {
        return PhasePipeline()
            .addPhase(StartCountPhase(true))
            .addPhase(DelayPhase(2))
            .addPhase(AbilityDrawPhase())
            .addPhase(RunnablePhase() {
                GameCore.game.currentGameMode = GameMode.SURVIVAL
                GameCore.game.pvpStarted = true
            })
            .addPhase(GamePhase())
            .addPhase(EndPhase())
    }

    override suspend fun init() {
        super.init()

        abilityDrawStarted = false
    }

}