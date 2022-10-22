package net.pooleaf.gamecore.player

import net.pooleaf.core.modules.support.common.manager.AbstractManager
import java.util.UUID

abstract class GamePlayerManager<T: GamePlayer>: AbstractManager<UUID, T>() {

    /**
     * [T]를 생성하고 캐싱한 뒤 반환합니다.
     */
    abstract fun create(uuid: UUID): T

    /**
     * [name]으로 [GamePlayer]를 찾아 반환합니다.
     */
    fun getByName(name: String): T? {
        return values().firstOrNull { it.name == name }
    }

    /**
     * 오프라인 [GamePlayer]를 포함한 게임에 참여 중인 [GamePlayer]의 [List]를 반환합니다.
     */
    fun getJoinedPlayers(): List<T> {
        return values().filter { it.joined }
            .toList()
    }

    /**
     * 참여 중인 온라인 [GamePlayer]의 [List]를 반환합니다.
     */
    fun getOnlineJoinedPlayers(): List<T> {
        return values().filter { it.joined && it.isOnline }
            .toList()
    }

}