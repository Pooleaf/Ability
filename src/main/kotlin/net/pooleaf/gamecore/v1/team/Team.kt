package net.pooleaf.gamecore.v1.team

import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.v1.GameCore
import net.pooleaf.gamecore.v1.player.GamePlayer
import org.bukkit.Location

open class Team {

    val players: HashSet<GamePlayer> = HashSet()
    var spawnLocation: Location? = null


    internal constructor()


    /**
     * 팀에 [player]를 추가합니다.
     */
    fun addPlayer(player: GamePlayer): Team {
        players.add(player)
        return this
    }

    /**
     * 팀에서 [player]를 제거합니다.
     */
    fun removePlayer(player: GamePlayer): Team {
        players.remove(player)
        if (players.size < 1) {
            GameCore.teamManager.remove(this)
        }

        return this
    }

    /**
     * 팀 전체를 [location]으로 텔레포트시킵니다.
     */
    fun teleport(location: Location) {
        players.filter { it.isOnline }
            .forEach { TeleportUtil.teleport(it.player, location) }
    }

    /**
     * 탈락한 팀인지 반환합니다.
     */
    fun isDefeated(): Boolean {
        return players.size == players.filter { !it.isPlaying() }.size
    }

    /**
     * 팀원 수를 반환합니다.
     */
    fun size(): Int {
        return players.size
    }

}