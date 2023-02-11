package net.pooleaf.gamecore.v1.player

import java.util.*

class DefaultGamePlayerManager: GamePlayerManager<GamePlayer>() {

    override fun create(uuid: UUID): GamePlayer {
        return GamePlayer(uuid)
    }

}