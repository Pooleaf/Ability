package net.pooleaf.ability.game

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.game.Game
import net.pooleaf.gamecore.phase.PhasePipeline
import net.pooleaf.gamecore.phase.RunnablePhase
import net.pooleaf.gamecore.phases.EndPhase
import net.pooleaf.gamecore.phases.GamePhase
import net.pooleaf.gamecore.phases.MapTeleportCountPhase
import net.pooleaf.gamecore.phases.StartCountPhase
import org.bukkit.GameMode

class AbilityGame: Game(100) {

    override fun createPhasePipeline(): PhasePipeline {
        return PhasePipeline()
            .addPhase(StartCountPhase(true))
            .addPhase(RunnablePhase() {
                GameCore.game.currentGameMode = GameMode.SURVIVAL
                GameCore.game.pvpStarted = true
            })
            .addPhase(GamePhase())
            .addPhase(EndPhase())
    }

}