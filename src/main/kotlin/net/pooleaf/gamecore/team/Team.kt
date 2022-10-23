package net.pooleaf.gamecore.team

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer

class Team {

    val players: HashSet<GamePlayer> = HashSet()


    fun addPlayer(player: GamePlayer): Team {
        players.add(player)
        return this
    }

    fun removePlayer(player: GamePlayer): Team {
        players.remove(player)
        if (players.size < 1) {
            GameCore.teamManager.remove(this)
        }

        return this
    }

    fun size(): Int {
        return players.size
    }

}