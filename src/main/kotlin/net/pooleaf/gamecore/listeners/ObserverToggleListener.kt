package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ObserverToggleListener: Listener {

    /**
     * 게임 중에 참여 안하고 접속할 경우 관전 모드로 전환
     */
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        GameCore.playerManager.get(event.player.uniqueId).let {
            if (GameCore.game.gameStarted && (!it.joined || it.defeated) // 게임 중에 들어오거나
                || GameCore.game.ended // 게임이 종료되고 들어오거나
                || GameCore.teamConfig.playerCountPerTeam * GameCore.teamConfig.maxTeamCount < GameCore.playerManager.getPlayingPlayers().size) { // (팀당 플레이어수 * 최대 팀 수)보다 참여자가 많으면
                it.toggleObserver(true)
            }
        }
    }

    /**
     * 관전 모드 상태에서 퇴장할 경우 관전 모드 해제
     */
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        GameCore.playerManager.get(event.player.uniqueId).let {
            if (it.observer) {
                it.toggleObserver(false)
            }
        }
    }

}