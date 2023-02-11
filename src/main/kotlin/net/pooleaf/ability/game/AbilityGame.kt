package net.pooleaf.ability.game

import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.gamecore.v1.GameCore
import net.pooleaf.gamecore.v1.game.Game
import net.pooleaf.gamecore.v1.phase.DelayPhase
import net.pooleaf.gamecore.v1.phase.PhasePipeline
import net.pooleaf.gamecore.v1.phase.RunnablePhase
import net.pooleaf.gamecore.v1.phases.EndPhase
import net.pooleaf.gamecore.v1.phases.GamePhase
import net.pooleaf.gamecore.v1.phases.StartCountPhase
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
                GameCore.game.isPvpStarted = true
            })
            .addPhase(GamePhase())
            .addPhase(EndPhase())
    }

    override suspend fun init() {
        super.init()

        abilityDrawStarted = false
    }

}