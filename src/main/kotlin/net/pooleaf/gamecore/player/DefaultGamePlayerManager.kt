package net.pooleaf.gamecore.player

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.team.Team
import java.util.*

class DefaultGamePlayerManager: GamePlayerManager<GamePlayer>() {

    override fun create(uuid: UUID): GamePlayer {
        if (exists(uuid)) {
            return get(uuid)
        }

        val gamePlayer = GamePlayer(uuid, Team())
        gamePlayer.team.addPlayer(gamePlayer)
        set(uuid, gamePlayer)

        GameCore.teamManager.add(gamePlayer.team)

        return gamePlayer
    }

}