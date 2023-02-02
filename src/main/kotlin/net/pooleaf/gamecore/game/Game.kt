package net.pooleaf.gamecore.game

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.DefaultTitleBuilder
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.*
import net.pooleaf.gamecore.map.GameMap
import net.pooleaf.gamecore.phase.PhasePipeline
import net.pooleaf.gamecore.phase.PhaseTask
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

abstract class Game {

    val gameType: Int
    var gameId: UUID? = null

    var initialized: Boolean = true

    // 게임 시작 카운트 시작 여부
    var countingStarted: Boolean = false
    // 게임 시작 여부
    var gameStarted: Boolean = false

    var mapTeleported: Boolean = false
    var pvpStarted: Boolean = false
    var ended: Boolean = false

    var startTime: LocalDateTime? = null

    var currentGameMode: GameMode = GameMode.ADVENTURE // 현재 상황의 게임 모드
    set(value) {
        field = value

        // 게임 중인 플레이어 게임 모드 변경
        Bukkit.getScheduler().runTask(GameCore.gamePlugin) {
            GameCore.playerManager.getOnlinePlayingPlayers().forEach { it.player.gameMode = value }
        }
    }

    var map: GameMap? = null

    var phaseTask: PhaseTask


    constructor(gameType: Int) {
        this.gameType = gameType
        this.phaseTask = PhaseTask(createPhasePipeline())
    }


    /**
     * 게임을 초기화시킵니다.
     */
    open suspend fun init() {
        initialized = false

        gameId = null

        countingStarted = false
        gameStarted = false
        mapTeleported = false
        pvpStarted = false
        ended = false

        startTime = null

        currentGameMode = GameMode.ADVENTURE

        // 스폰으로 텔레포트
        BukkitSyncScope.async {
            GameCore.spawnConfig.spawnLocation.let {
                Bukkit.getOnlinePlayers().forEach {
                    TeleportUtil.teleport(it, GameCore.spawnConfig.spawnLocation)
                }
            }
        }.await()

        // 맵 언로드
        if (map != null && !map!!.unload()) {
            // 맵 언로드 실패 시 서버 재부팅
            Broadcaster.broadcastTitle(
                DefaultTitleBuilder()
                    .title("§c오류")
                    .subtitle("§c게임 초기화에 실패하여 서버가 재부팅됩니다.")
                    .stay(10 * 20)
                    .build()
            )
            initialized = false

            // 10초 후 서버 종료
            BukkitAsyncScope.launch {
                delay(10_000L)
                // TODO 로비로 텔레포트
                Bukkit.shutdown()
            }
        }
        map = null

        // Phase 초기화
        phaseTask.cancel()
        phaseTask = PhaseTask(createPhasePipeline())

        // 플레이어 초기화
        GameCore.playerManager.values().forEach {
            it.init()
            it.joined = true

            // 오프라인일 경우 삭제
            if (!it.isOnline) {
                GameCore.playerManager.remove(it.uuid)
            }
        }

        initialized = true
    }


    /**
     * 이 [Game]의 [Phase] 순서대로 나열된 [PhasePipeline]을 생성합니다.
     */
    abstract fun createPhasePipeline(): PhasePipeline


    /**
     * 게임이 시작한 직후 호출됩니다.
     * [starter]가 null일 경우 자동으로 시작한 게임으로 취급합니다.
     * [GameCountingStartedEvent]보다 먼저 실행됩니다.
     */
    open fun onStarted(starter: CommandSender?) {}

    /**
     * 게임이 취소된 직후 호출됩니다.
     * [GameCancelledEvent]보다 먼저 실행됩니다.
     */
    open fun onCancelled() {}

    /**
     * 게임이 끝난 직후 호출됩니다.
     * [GameEndedEvent]보다 먼저 호출됩니다.
     */
    open fun onEnded() {}

    /**
     * 게임이 끝나고 게임이 리셋될 때 호출됩니다.
     * [GameEndResetEvent]보다 먼저 호출됩니다.
     */
    open fun onEndReset() {}


    /**
     * 게임을 시작할 수 있는지 확인합니다.
     */
    open fun canStart(): Boolean {
        return initialized
                && !GameCore.game.countingStarted
                && GameCore.playerManager.getOnlinePlayingPlayers().size >= GameCore.autoGameConfig.startPlayerCount
    }

    /**
     * 게임을 시작시킵니다.
     * [starter]가 null일 경우 자동으로 시작한 게임으로 취급합니다.
     */
    fun start(starter: CommandSender?): Boolean {
        if (countingStarted) return false

        // 맵 설정
        if (map == null) {
            map = GameCore.mapManager.getRandom()
            if (map == null) {
                Broadcaster.broadcastTitle(
                    DefaultTitleBuilder()
                        .title("§c시작 실패")
                        .subtitle("§c사용할 수 없는 맵이 없어 게임을 시작할 수 없습니다.")
                        .stay(5 * 20)
                        .build()
                )
                Broadcaster.broadcastSound(XSound.ENTITY_ITEM_BREAK, 1F, 1F)
                return false
            }

            map!!.load()
        }
        // 맵 사용 가능 여부 체크
        else if (!map!!.canUse()) {
            Broadcaster.broadcastTitle(
                DefaultTitleBuilder()
                    .title("§c시작 실패")
                    .subtitle("§f${map!!.displayName} §c맵을 사용할 수 없어 게임이 중단되었습니다.")
                    .stay(5 * 20)
                    .build()
            )
            Broadcaster.broadcastSound(XSound.ENTITY_ITEM_BREAK, 1F, 1F)
            return false
        }

        // 시작 정보
        gameId = UUID.randomUUID()

        countingStarted = true
        startTime = LocalDateTime.now()

        // ActionBar 제거
        Broadcaster.removeActionBar()

        // Phase 시작
        phaseTask.start()

        // 이벤트
        onStarted(starter)
        Bukkit.getPluginManager().callEvent(GameCountingStartedEvent(starter))

        // TODO 게임 DB 저장

        return true
    }

    /**
     * 게임을 끝낼 수 있는지를 반환합니다.
     */
    open fun canEnd(): Boolean {
        return gameStarted && !ended
                && System.currentTimeMillis() - startTime!!.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() > GameCore.autoGameConfig.winAllowTime * 1000 // 우승 가능 시간인지 체크
                && GameCore.teamManager.getNotDefeatedOnlineTeams().size == 1 // 한 팀만 남았는지 체크
    }

    /**
     * 남은 팀을 우승시키고 게임을 종료시킵니다.
     */
    fun end(): Boolean {
        if (!countingStarted || !canEnd() || ended) return false

        ended = true

        // 이벤트
        onEnded()
        Bukkit.getPluginManager().callEvent(GameEndedEvent())

        // TODO 게임 DB 저장

        return true
    }

    /**
     * 게임이 종료되고 나서 게임을 리셋 시킵니다.
     */
    suspend fun endReset(): Boolean {
        if (!ended) return false

        // 이벤트
        onEndReset()
        Bukkit.getPluginManager().callEvent(GameEndResetEvent())

        // 초기화
        init()

        // 대기 액션바
        Broadcaster.broadcastWaitingActionBar()

        // 게임 시작 조건에 충족할 경우 1초 뒤 재시작
        BukkitSyncScope.launch {
            delay(1000L)

            if (canStart()) {
                start(null)
            }
        }

        return true
    }

    /**
     * 게임을 중단시킵니다.
     */
    suspend fun cancel(): Boolean {
        if (!countingStarted) return false

        // 이벤트
        onCancelled()
        Bukkit.getPluginManager().callEvent(GameCancelledEvent())

        // 초기화
        init()

        // 대기 액션바
        Broadcaster.broadcastWaitingActionBar()

        // TODO 게임 DB 저장

        return true
    }

    fun onPlayerJoin() {
        if (GameCore.game.canStart()) {
            GameCore.game.start(null)
        }
    }

    fun onPlayerLeft() {
        // 우승 가능한 시간이 지나야만 우승
        if (GameCore.game.canEnd()) {
            GameCore.game.end()
        }
        // 우승 불가능하고 한 팀만 남으면 게임 중단
        else if (GameCore.game.countingStarted && !GameCore.game.ended
            && ((GameCore.game.gameStarted && GameCore.teamManager.getNotDefeatedOnlineTeams().size <= 1)
                    || (!GameCore.game.gameStarted && GameCore.playerManager.getOnlinePlayingPlayers().size <= 1))) {
            BukkitAsyncScope.launch {
                GameCore.game.cancel()

                Broadcaster.broadcastTitle(
                    DefaultTitleBuilder()
                        .title("§c게임 중단")
                        .subtitle("§c게임 조건이 충족되지 않아 게임이 중단되었습니다.")
                        .stay(5 * 20)
                        .build()
                )
                Broadcaster.broadcastSound(XSound.ENTITY_ITEM_BREAK, 1F, 1F)
            }
        }
        // 게임 중에 한 팀 빼고 퇴장하면 종료
    }

    suspend fun teleportToMap() {
        // 맵이 없을 경우 게임 중단
        if (map == null) {
            BukkitAsyncScope.launch {
                Broadcaster.broadcastTitle(
                    DefaultTitleBuilder()
                        .title("§c시작 실패")
                        .subtitle("§c맵이 설정되지 않아 게임이 중단되었습니다.")
                        .stay(5 * 20)
                        .build()
                )
                Broadcaster.broadcastSound(XSound.ENTITY_ITEM_BREAK, 1F, 1F)

                GameCore.game.cancel()
            }
        }
        // 맵으로 이동
        else {
            val map = GameCore.game.map!!

            // 관전자 맵으로 텔레포트
            GameCore.playerManager.getObservers().forEach { TeleportUtil.teleport(it.player, map.getCenterLocation()) }

            // 팀끼리 맵으로 텔레포트
            BukkitSyncScope.async {
                GameCore.teamManager.teams.forEach {team ->
                    val location = map.getRandomLocation()
                    team.spawnLocation = location
                    team.teleport(location)
                }
            }.await()

            Broadcaster.broadcastActionBar(map.displayName + " §e맵으로 이동되었습니다.")

            mapTeleported = true

            // 이벤트
            Bukkit.getPluginManager().callEvent(GameMapTeleportedEvent())
        }
    }

}