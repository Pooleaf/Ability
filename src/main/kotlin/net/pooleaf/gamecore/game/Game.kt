package net.pooleaf.gamecore.game

import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameCancelledEvent
import net.pooleaf.gamecore.events.game.GameEndResetEvent
import net.pooleaf.gamecore.events.game.GameEndedEvent
import net.pooleaf.gamecore.events.game.GameStartedEvent
import net.pooleaf.gamecore.map.GameMap
import net.pooleaf.gamecore.phase.PhasePipeline
import net.pooleaf.gamecore.phase.PhaseTask
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import java.time.LocalDateTime
import java.util.*

abstract class Game {

    val gameType: Int
    var gameId: UUID? = null

    var countingStarted: Boolean = false
    var gameStarted: Boolean = false
    var ended: Boolean = false

    var startTime: LocalDateTime? = null

    var map: GameMap? = null

    var phaseTask: PhaseTask


    constructor(gameType: Int) {
        this.gameType = gameType
        this.phaseTask = PhaseTask(createPhasePipeline())
    }


    abstract fun createPhasePipeline(): PhasePipeline

    /**
     * 게임이 시작한 직후 호출됩니다.
     * [GameStartedEvent]보다 먼저 실행됩니다.
     */
    open fun onStarted() {}

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
     * 게임을 초기화시킵니다.
     */
    open fun init() {
        gameId = null

        countingStarted = false
        gameStarted = false
        ended = false

        startTime = null

        map?.unload()
        map = null

        phaseTask.cancel()
        phaseTask = PhaseTask(createPhasePipeline())

        // 플레이어 초기화
        GameCore.playerManager.values().forEach {
            it.joined = true
            it.defeated = false
            it.observer = false

            Bukkit.getScheduler().runTask(GameCore.gamePlugin) {
                val player = it.player

                player?.health = player.maxHealth
                player?.gameMode = GameMode.ADVENTURE
                player.level = 0
                player.exp = 0F
                player?.inventory!!.clear()
                player?.updateInventory()
            }

            // 오프라인일 경우 삭제
            if (!it.isOnline) {
                GameCore.playerManager.remove(it.uuid)
            }
        }
    }

    /**
     * 게임을 시작시킵니다.
     */
    fun start(starter: CommandSender): Boolean {
        if (countingStarted) return false

        // 맵 설정
        if (map == null) {
            map = GameCore.mapManager.getRandom()
            if (map == null) {
                Broadcaster.broadcast("§c사용할 수 없는 맵이 없어 게임이 중단되었습니다.")
                return false
            }

            map!!.load()
        }
        // 맵 사용 가능 여부 체크
        else if (!map!!.canUse()) {
            Broadcaster.broadcast("§6${map!!.name} §c맵을 사용할 수 없어 게임이 중단되었습니다.")
            return false
        }

        // 시작 정보
        gameId = UUID.randomUUID()

        countingStarted = true
        startTime = LocalDateTime.now()

        // Phase 시작
        phaseTask.start()

        // 이벤트
        onStarted()
        Bukkit.getPluginManager().callEvent(GameStartedEvent(starter))

        // TODO 게임 DB 저장

        return true
    }

    /**
     * 게임을 끝낼 수 있는지를 반환합니다.
     */
    open fun canEnd(): Boolean {
        return GameCore.teamManager.getNotDefeatedTeams().size == 1
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
    fun endReset(): Boolean {
        if (!ended) return false

        // 이벤트
        onEndReset()
        Bukkit.getPluginManager().callEvent(GameEndResetEvent())

        // 스폰으로 텔레포트
        Bukkit.getOnlinePlayers().forEach { TeleportUtil.teleport(it, GameCore.gameConfig.spawnLocation) }

        // 초기화
        init()

        // TODO 스폰으로 텔레포트

        return true
    }

    /**
     * 게임을 중단시킵니다.
     */
    fun cancel(): Boolean {
        if (!countingStarted) return false

        // 이벤트
        onCancelled()
        Bukkit.getPluginManager().callEvent(GameCancelledEvent())

        // 스폰으로 텔레포트
        Bukkit.getOnlinePlayers().forEach { TeleportUtil.teleport(it, GameCore.gameConfig.spawnLocation) }

        // 초기화
        init()

        // TODO 게임 DB 저장

        return true
    }

}