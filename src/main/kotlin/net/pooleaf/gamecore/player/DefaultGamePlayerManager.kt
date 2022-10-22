package net.pooleaf.gamecore.player

import java.util.*

class DefaultGamePlayerManager: GamePlayerManager<GamePlayer>() {

    override fun create(uuid: UUID): GamePlayer {
        if (exists(uuid)) {
            return get(uuid)
        }

        val gamePlayer = GamePlayer(uuid)
        set(uuid, gamePlayer)
        return gamePlayer
    }

}