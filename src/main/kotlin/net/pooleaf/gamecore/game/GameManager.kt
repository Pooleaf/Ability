package net.pooleaf.gamecore.game

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.gui.GuiModule
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.*
import net.pooleaf.gamecore.team.Team
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class GameManager {

    lateinit var game: net.pooleaf.gamecore.game.Game
        internal set


    /**
     * 게임 정보를 초기화 시킵니다.
     */
    fun initGame() {
        // 게임 정보 초기화
        game.gameId = null

        game.isCountingStarted = false
        game.isGameStarted = false
        game.isTeleportedToMap = false
        game.isPvpStarted = false
        game.isEnded = false

        game.startedAt = null
        game.endedAt = null

        game.currentGameMode = game.waitingGameMode

        // 이벤트
        Bukkit.getPluginManager().callEvent(GameInitEvent())
    }

    /**
     * 게임을 리셋시킵니다.
     */
    suspend fun resetGame() {
        // 스폰으로 텔레포트
        BukkitSyncScope.launch {
            GameCore.spawnConfig.spawnLocation?.let { location ->
                Bukkit.getOnlinePlayers().forEach { player -> TeleportUtil.teleport(player, location) }
            }
        }.join()

        // 맵 언로드
        GameCore.currentMap?.let { map ->
            // 언로드 실패 시 서버 재부팅
            if (!map.unloadWorld()) {
                Broadcaster.broadcastTitle("§c오류", "§c게임 초기화에 실패하여 서버가 재부팅됩니다.", 10 * 20)

                // 10초 후 서버 종료
                BukkitAsyncScope.launch {
                    // 로비로 이동
                    delay(8000L)
                    Bukkit.getOnlinePlayers().forEach { ChannelModule.getLobbyChannelGroup().fastJoin(it.uniqueId) }

                    // 서버 종료
                    delay(2000L)
                    Bukkit.shutdown()
                }.join()
            }
        }
        GameCore.unsafe.mapManager.currentMap = null

        // Phase 초기화
        if (game.phasePipeline.isRunning()) {
            game.phasePipeline.cancelPhases()
        }
        game.phasePipeline.init()

        // 플레이어 초기화
        GameCore.unsafe.playerManager.values().forEach { gamePlayer ->
            gamePlayer.init()

            if (gamePlayer.isOnline) {
                gamePlayer.isJoined = true
                gamePlayer.reset()

                // 대기 퀵바
                GameCore.unsafe.quickBarManager.waitingQuickBar.setTo(gamePlayer.player)
            } else {
                GameCore.unsafe.playerManager.remove(gamePlayer.uuid)
            }
        }

        // 대기 액션바
        Broadcaster.broadcastWaitingActionBar(GameCore.unsafe.playerManager.getOnlineJoinedPlayers().size, GameCore.autoGameConfig.startPlayerCount)

        // 투표 초기화
        GameCore.unsafe.startVoteManager.initVote()
        GameCore.unsafe.mapVoteManager.initVote()

        // 이벤트
        Bukkit.getPluginManager().callEvent(GameResetEvent())
    }


    /**
     * 게임을 시작시킵니다.
     */
    suspend fun startGame(starterSender: CommandSender?) {
        if (game.phasePipeline.isRunning()) error("Game has already started")

        // 설정된 맵 없으면 랜덤 맵으로 설정
        if (GameCore.currentMap == null) {
            GameCore.unsafe.mapManager.currentMap = GameCore.unsafe.mapManager.getRandomMapCanUse()
        }
        // 맵 사용 가능 체크
        if (GameCore.currentMap?.canUse == false) {
            GameCore.unsafe.mapManager.currentMap = null
        }
        GameCore.currentMap?.let {
            // 월드 로드
            if (!it.isWorldLoaded()) {
                it.loadWorld()
            }
        } ?: run {
            // 맵 없으면 중단
            Broadcaster.broadcastTitle(
                "§c시작 실패",
                "§c사용할 수 있는 맵이 없어 게임을 시작할 수 없습니다.",
                5 * 20
            )
            Broadcaster.broadcastSound(XSound.ENTITY_ITEM_BREAK)
            return
        }

        // 게임 정보 업데이트
        game.gameId = UUID.randomUUID()

        // 액션바 제거
        Broadcaster.removeActionBar()

        // Phase 시작
        game.phasePipeline.runPhases()

        // 이벤트
        Bukkit.getPluginManager().callEvent(GameStartEvent(starterSender))
    }

    /**
     * 게임 시작 시 실행됩니다.
     */
    fun onGameStarted() {
        // 팀 매칭
        GameCore.unsafe.teamService.matchingTeams(GameCore.teamConfig.playerCountPerTeam, GameCore.teamConfig.maxTeamCount)

        // 게임 정보 업데이트
        game.isGameStarted = true
        game.startedAt = LocalDateTime.now()

        // TODO 게임 정보 저장

        // 액션바 제거
        Broadcaster.removeActionBar()

        // 퀵바 제거
        Bukkit.getOnlinePlayers().forEach { GuiModule.getQuickBarManager().removeTo(it) }

        // 관전자 설정
        GameCore.unsafe.playerManager.getOnlineObservers().forEach {
            // 관전 퀵바
            GameCore.unsafe.quickBarManager.observerQuickBar.setTo(it.player)

            // 관전자 날기 활성화
            it.player.isFlying = true
        }

        // 이벤트
        Bukkit.getPluginManager().callEvent(GameStartedEvent())
    }

    /**
     * 게임 자동 시작 가능 여부를 반환합니다.
     */
    fun canAutoStart(): Boolean {
        return !GameCore.game.isRunning
                && GameCore.unsafe.playerManager.getOnlineJoinedPlayers().size >= GameCore.autoGameConfig.startPlayerCount
    }

    /**
     * 게임 종료 가능 여부를 반환합니다.
     */
    fun canEnd(): Boolean {
        return game.isGameStarted && !game.isEnded
                && GameCore.unsafe.teamManager.getNotDefeatedOnlineTeams().size == 1
    }

    /**
     * 우승 가능 여부를 반환합니다.
     */
    fun isWinAllowTime(): Boolean {
        return game.startedAt?.let { System.currentTimeMillis() - it.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() > GameCore.autoGameConfig.winAllowTime * 1000 } == true
    }

    /**
     * 게임 종료 시 실행됩니다.
     * 우승 팀을 반환합니다.
     */
    fun onGameEnd(): Team? {
        if (!canEnd()) error("End of game condition not met")

        // 게임 정보 업데이트
        game.isEnded = true
        game.endedAt = LocalDateTime.now()

        // 우승자 계산
        val winnerTeam = GameCore.unsafe.teamManager.getNotDefeatedOnlineTeams().firstOrNull()

        // 게임 종료 이벤트
        Bukkit.getPluginManager().callEvent(GameEndEvent(winnerTeam))

        return winnerTeam
    }

    /**
     * 게임을 중단시킵니다.
     */
    suspend fun cancelGame(cancelSender: CommandSender?, cancelCause: String = "게임이 중단되었습니다.") {
        if (!GameCore.game.isRunning) error("Game is not started")

        resetGame()

        // 타이틀
        Broadcaster.broadcastTitle("§c게임 중단", "§c${cancelCause}", 5 * 20)
        Broadcaster.broadcastSound(XSound.ENTITY_ITEM_BREAK, 1.0F, 1.0F)

        // 이벤트
        Bukkit.getPluginManager().callEvent(GameCancelledEvent(cancelSender, cancelCause))
    }

    /**
     * 팀끼리 맵으로 텔레포트시킵니다.
     */
    suspend fun teleportToMap() {
        val map = GameCore.currentMap

        map?.let {
            BukkitSyncScope.launch {
                // 관전자 맵으로 텔레포트
                GameCore.unsafe.playerManager.getOnlineObservers().forEach { TeleportUtil.teleport(it.player, map.getCenterLocation()) }

                // 팀끼리 맵으로 텔레포트
                GameCore.unsafe.teamManager.teams.forEach { team ->
                    val location = map.getRandomLocation()
                    location?.let {
                        team.spawnLocation = location
                        team.teleport(location)
                    } ?: error("location cannot be null")
                }
            }.join()

            // 액션바
            Broadcaster.broadcastActionBar("${map.displayName} §e맵으로 이동되었습니다.")

            // 게임 정보 업데이트
            game.isTeleportedToMap = true

            // 이벤트
            Bukkit.getPluginManager().callEvent(GameMapTeleportedEvent())
        } ?: error("currentMap cannot be null")
    }

    suspend fun changeGameMode(gameMode: GameMode) {
        GameCore.game.currentGameMode = gameMode
        GameCore.unsafe.playerManager.getPlayingPlayers().forEach { it.player?.gameMode = gameMode }
    }

}