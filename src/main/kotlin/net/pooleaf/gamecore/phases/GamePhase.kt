package net.pooleaf.gamecore.phases

import kotlinx.coroutines.delay
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase

class GamePhase: Phase() {

    override suspend fun onStart() {
        while (!GameCore.game.isEnded) {
            delay(100L)
        }
    }

}