package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinGameModeChangeListener: Listener {

    /**
     * 게임 접속 시 상황에 맞는 게임 모드 강제 적용
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onJoin(event: PlayerJoinEvent) {
        GameCore.playerManager.get(event.player.uniqueId).let {
            if (!it.observer) {
                event.player.gameMode = GameCore.game.currentGameMode
            }
        }
    }

}