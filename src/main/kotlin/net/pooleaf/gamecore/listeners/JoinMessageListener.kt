package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinMessageListener: Listener {

    /**
     * 접속 메시지 설정
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun join(event: PlayerJoinEvent) {
        val gamePlayer = GameCore.playerManager.get(event.player.uniqueId)

        event.joinMessage = when {
            // 관전 모드 접속
            gamePlayer.observer -> "§f${gamePlayer.displayName}§b님께서 관전 모드로 접속했습니다."

            // 게임 중 재접속
            GameCore.game.gameStarted && gamePlayer.joined && !gamePlayer.defeated -> "§f${gamePlayer.displayName} §e님께서 재접속했습니다."

            // 대기 중 접속
            !GameCore.game.countingStarted -> {
                Broadcaster.broadcastWaitingActionBar()

                "§f${gamePlayer.displayName} §e님께서 접속했습니다. §f(${GameCore.playerManager.getOnlinePlayingPlayers().size}/${GameCore.autoGameConfig.startPlayerCount})"
            }

            // 기본
            else -> "§f${gamePlayer.displayName} §e님께서 접속했습니다."
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun quit(event: PlayerQuitEvent) {
        val gamePlayer = GameCore.playerManager.get(event.player.uniqueId)

        event.quitMessage = when {
            // 관전 모드 퇴장
            gamePlayer.observer -> "§f${gamePlayer.displayName}§b님께서 관전을 종료했습니다."

            // 대기 중 퇴장
            !GameCore.game.countingStarted -> {
                Broadcaster.broadcastWaitingActionBar(GameCore.playerManager.getOnlinePlayingPlayers().size - 1)

                "§f${gamePlayer.displayName} §e님께서 퇴장했습니다. §f(${GameCore.playerManager.getOnlinePlayingPlayers().size - 1}/${GameCore.autoGameConfig.startPlayerCount})"
            }

            // 기본
            else -> "§f${gamePlayer.displayName} §e님께서 퇴장했습니다."
        }
    }

}