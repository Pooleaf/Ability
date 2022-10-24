package net.pooleaf.gamecore.listeners

import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinTeleportListener: Listener {

    /**
     * 접속 시 상황에 맞는 장소로 텔레포트시킵니다.
     */
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val gamePlayer = GameCore.playerManager.get(player.uniqueId)

        when {
            // 대기 중 스폰
            !GameCore.game.mapTeleported && GameCore.spawnConfig.spawnLocation != null -> TeleportUtil.teleport(player, GameCore.spawnConfig.spawnLocation)

            // 관전자 게임 시작 후엔 맵으로
            GameCore.game.mapTeleported && gamePlayer.observer -> TeleportUtil.teleport(player, GameCore.game.map!!.getCenterLocation())

            // 게임 중인 플레이어 텔레포트 안됐으면 팀 스폰으로
            GameCore.game.mapTeleported && gamePlayer.isPlaying() && !GameCore.game.map!!.isInRadius(player.location) -> {
                TeleportUtil.teleport(player, gamePlayer.team?.spawnLocation)
            }
        }
    }

}