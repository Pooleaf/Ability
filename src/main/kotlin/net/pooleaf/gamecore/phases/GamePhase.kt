package net.pooleaf.gamecore.phases

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase

class GamePhase: Phase() {

    override fun onRun() {
        if (GameCore.game.ended) {
            end()
        }
    }

}