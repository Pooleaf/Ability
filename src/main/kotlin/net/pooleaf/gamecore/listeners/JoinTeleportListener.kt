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

        when {
            // 대기 중 스폰
            !GameCore.game.gameStarted && GameCore.spawnConfig.spawnLocation != null -> TeleportUtil.teleport(player, GameCore.spawnConfig.spawnLocation)

            // 게임 시작 후엔 맵으로
            GameCore.game.gameStarted -> TeleportUtil.teleport(player, GameCore.game.map!!.getCenterLocation())
        }
    }

}