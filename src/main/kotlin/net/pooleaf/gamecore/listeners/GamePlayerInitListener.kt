package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class GamePlayerInitListener: Listener {

    /**
     * [GamePlayer] 등록
     */
    @EventHandler(priority = EventPriority.LOW)
    fun onJoin(event: PlayerJoinEvent) {
        if (!GameCore.playerManager.exists(event.player.uniqueId)) {
            val gamePlayer = GameCore.playerManager.create(event.player.uniqueId)

            // 게임 중일 경우 관전모드로 전환
            if (GameCore.game.gameStarted) {
                // TODO 관전모드
            }
        }
    }

    /**
     * [GamePlayer] 등록 해제
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onQuit(event: PlayerQuitEvent) {
        if (!GameCore.game.countingStarted) {
            GameCore.playerManager.remove(event.player.uniqueId)
        }
    }

}