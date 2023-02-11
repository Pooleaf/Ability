package net.pooleaf.gamecore.player

import kotlinx.coroutines.Job
import net.pooleaf.core.modules.support.bukkit.player.AbstractBukkitPlayer
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.team.Team
import java.util.*

open class GamePlayer(uuid: UUID) : AbstractBukkitPlayer(uuid) {

    // 게임 참여 여부
    var isJoined = false
        internal set

    // 탈락 여부
    var isDefeated = false
        internal set

    // 관전 여부
    var isSpectator = false
        internal set

    // 팀
    var team: Team? = null
        internal set

    // 재접속 Job
    var reconnectJob: Job? = null
        internal set


    /**
     * 게임 플레이 중 여부를 반환합니다.
     */
    fun isPlaying(): Boolean {
        return isJoined && !isDefeated && !isSpectator
    }

    /**
     * 플레이어 정보를 초기화합니다.
     */
    fun init() {
        GameCore.unsafe.playerService.initPlayer(this)
    }

    /**
     * 플레이어 게임 상태를 리셋합니다.
     * 온라인 플레이어만 사용 가능합니다.
     */
    suspend fun reset() {
        GameCore.unsafe.playerService.resetPlayer(this)
    }

    /**
     * 플레이어의 관전 모드를 활성화합니다.
     */
    suspend fun enableSpectatorMode() {
        GameCore.unsafe.playerService.enableSpectatorMode(this)
    }

    /**
     * 플레이어의 관전 모드를 비활성화합니다.
     */
    suspend fun disableSpectatorMode() {
        GameCore.unsafe.playerService.disableSpectatorMode(this)
    }

}