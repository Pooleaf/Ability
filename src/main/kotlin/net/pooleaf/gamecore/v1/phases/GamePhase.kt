package net.pooleaf.gamecore.v1.phases

import net.pooleaf.gamecore.v1.GameCore
import net.pooleaf.gamecore.v1.phase.Phase

open class GamePhase: Phase() {

    override fun onRun() {
        if (GameCore.game.isEnded) {
            end()
        }
    }

}