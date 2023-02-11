package net.pooleaf.gamecore.listeners

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class GamePlayerListener: Listener {

    /**
     * GamePlayer 등록 및 리셋
     * -> 게임 중이 아니라면? -> 게임 참여 대기 셋팅 -> 자동 시작
     * -> 게임 중이라면? -> 참여했다면? -> 게임 참여 게임 중 셋팅
     *                    안했다면?  -> 관전 등록
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        var gamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)

        val isNewPlayer = (gamePlayer == null)

        BukkitSyncScope.launch {
            println("1111111: ${player.name}")
            // 새로운 GamePlayer라면 등록 및 초기화
            if (isNewPlayer) {
                gamePlayer = GameCore.unsafe.playerManager.gamePlayerFactory.createGamePlayer(player.uniqueId)
                GameCore.unsafe.playerManager.set(player.uniqueId, gamePlayer)

                gamePlayer.init()
            }

            // 리셋
            gamePlayer.reset()

            // 게임 중이 아니라면
            if (!GameCore.game.isGameStarted) {
                println("22222: ${player.name}")
                // 게임에 참여
                GameCore.unsafe.playerService.joinToGame(gamePlayer)
            }
            // 게임 중이라면
            else {
                // 게임에 참여했다면
                if (!isNewPlayer && gamePlayer.isPlaying()) {
                    // 게임 중 셋팅
                    GameCore.unsafe.playerService.settingToPlaying(gamePlayer)

                    // 재접속 타이머 해제
                    gamePlayer.reconnectJob?.cancel()
                    gamePlayer.reconnectJob = null
                }
                // 참여하지 않았다면
                else {
                    GameCore.unsafe.playerService.enableSpectatorMode(gamePlayer)
                }
            }

            // 이벤트
            Bukkit.getPluginManager().callEvent(
                net.pooleaf.gamecore.events.player.GamePlayerJoinEvent(
                    gamePlayer,
                    event
                )
            )
            println("333333: ${player.name}")
        }
    }

    /**
     * 게임 중이 아니라면? -> GamePlayer 삭제
     * 게임 중이라면? -> 참여했다면? -> 살아있다면? -> 재접속 타이머 시작
     *                              탈락했다면? ->
     *                 안했다면? -> GamePlayer 삭제
     */
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val gamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)

        BukkitSyncScope.launch {
            // 게임 중이 아니라면
            if (!GameCore.game.isGameStarted) {
                GameCore.unsafe.playerManager.remove(player.uniqueId)
            }
            // 게임 중이라면
            else {
                // 게임에 참여했다면
                if (gamePlayer.isJoined) {
                    // 살아있다면 재접속 타이머
                    if (gamePlayer.isPlaying()) {
                        gamePlayer.reconnectJob = BukkitAsyncScope.launch {
                            delay(GameCore.autoGameConfig.reconnectAllowSeconds * 1000L)
                            GameCore.unsafe.playerService.defeatPlayer(gamePlayer)
                            Broadcaster.broadcast("§c${gamePlayer.displayName} 님께서 재접속하지 않아 탈락했습니다.")
                            Broadcaster.broadcastSound(XSound.BLOCK_NOTE_BLOCK_BASS)
                        }
                    }

                    // 기록이나 재접속을 위해 GamePlayer 보존
                }
                // 참여하지 않았다면
                else {
                    GameCore.unsafe.playerManager.remove(player.uniqueId)
                }
            }

            // 이벤트
            Bukkit.getPluginManager().callEvent(
                net.pooleaf.gamecore.events.player.GamePlayerQuitEvent(
                    gamePlayer,
                    event
                )
            )

            // 관전 해제
            if (gamePlayer.isSpectator) {
                GameCore.unsafe.playerService.disableSpectatorMode(gamePlayer)
            }
        }
    }

}