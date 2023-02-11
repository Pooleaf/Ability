package net.pooleaf.gamecore.v1.listeners

import net.pooleaf.gamecore.v1.GameCore
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class AutoGameListener: Listener {

    /**
     * 게임 시작
     */
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        // 대기 중에 일정 인원이 되면 게임 시작
        GameCore.game.onPlayerJoin()
    }

    /**
     * 게임 종료
     */
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        // Player가 오프라인 처리되고 계산되어야 하기 때문에 1 Tick 뒤에 실행함
        Bukkit.getScheduler().runTaskLater(GameCore.gamePlugin, {
            GameCore.game.onPlayerLeft()
        }, 1L)
    }

}