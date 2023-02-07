package net.pooleaf.gamecore.player

import net.pooleaf.core.modules.support.common.manager.AbstractManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class GamePlayerManager<T: GamePlayer>: AbstractManager<UUID, T>() {

    init {
        datas = ConcurrentHashMap()
    }

    /**
     * [T]를 생성하고 반환합니다.
     */
    abstract fun create(uuid: UUID): T

    override fun remove(key: UUID?): Boolean {
        get(key).let {
            it.team?.removePlayer(it)
            super.remove(key)

            return true
        }

        return false
    }

    /**
     * [name]으로 [GamePlayer]를 찾아 반환합니다.
     */
    fun getByName(name: String): T? {
        return values().firstOrNull { it.name == name }
    }


    /**
     * 온라인 [GamePlayer]의 [List]를 반환합니다.
     */
    fun getOnlinePlayers(): List<T> {
        return values().filter { it.isOnline }
            .toList()
    }

    /**
     * 오프라인 [GamePlayer]를 포함한 게임을 플레이 중인 [GamePlayer]의 [List]를 반환합니다.
     */
    fun getPlayingPlayers(): List<T> {
        return values().filter { it.isPlaying() }
            .toList()
    }

    /**
     * 게임을 플레이 중인 온라인 [GamePlayer]의 [List]를 반환합니다.
     */
    fun getOnlinePlayingPlayers(): List<T> {
        return values().filter { it.isPlaying() && it.isOnline }
            .toList()
    }


    /**
     * 오프라인 [GamePlayer]를 포함한 게임에 참여한 [GamePlayer]의 [List]를 반환합니다.
     * 탈락하거나 관전 중인 플레이어를 포함합니다.
     */
    fun getJoinedPlayers(): List<T> {
        return values().filter { it.joined }
            .toList()
    }

    /**
     * 참여 중인 온라인 [GamePlayer]의 [List]를 반환합니다.
     * 탈락하거나 관전 중인 플레이어를 포함합니다.
     */
    fun getOnlineJoinedPlayers(): List<T> {
        return values().filter { it.joined && it.isOnline }
            .toList()
    }

    /**
     * 관전 중인 [GamePlayer]를 반환합니다.
     * 관전 중이던 [GamePlayer]는 퇴장 시 관전이 해제되므로 온라인 상태인 [GamePlayer]만 반환됩니다.
     */
    fun getObservers(): List<T> {
        return values().filter { it.observer && it.isOnline }
    }

}