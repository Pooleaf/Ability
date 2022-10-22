package net.pooleaf.gamecore.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinQuitMessageListener: Listener {

    /**
     * 접속 메시지 설정
     */
    @EventHandler
    fun join(event: PlayerJoinEvent) {
        // TODO 대기 중
        // TODO 게임 중 재접속
        // TODO 관전모드 접속
    }

    @EventHandler
    fun quit(event: PlayerQuitEvent) {
        // TODO 대기 중
        // TODO 게임 중 퇴장
        // TODO 관전모드 퇴장
    }

}