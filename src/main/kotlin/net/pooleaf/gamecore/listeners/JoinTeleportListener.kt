package net.pooleaf.gamecore.listeners

import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
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
            GameCore.game.mapTeleported && !gamePlayer.joined -> TeleportUtil.teleport(player, GameCore.game.map!!.getCenterLocation())
        }
    }

}