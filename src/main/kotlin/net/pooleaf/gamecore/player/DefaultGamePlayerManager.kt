package net.pooleaf.gamecore.player

import java.util.*

class DefaultGamePlayerManager: GamePlayerManager<GamePlayer>() {

    override fun create(uuid: UUID): GamePlayer {
        return GamePlayer(uuid)
    }

}