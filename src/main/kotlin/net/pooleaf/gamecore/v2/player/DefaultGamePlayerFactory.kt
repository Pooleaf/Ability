package net.pooleaf.gamecore.v2.player

import java.util.*

class DefaultGamePlayerFactory: GamePlayerFactory<GamePlayer> {

    override fun createGamePlayer(uuid: UUID): GamePlayer {
        return GamePlayer(uuid)
    }

}