package net.pooleaf.gamecore.game

import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.DefaultTitleBuilder
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameCancelledEvent
import net.pooleaf.gamecore.events.game.GameEndResetEvent
import net.pooleaf.gamecore.events.game.GameEndedEvent
import net.pooleaf.gamecore.events.game.GameCountingStartedEvent
import net.pooleaf.gamecore.map.GameMap
import net.pooleaf.gamecore.phase.PhasePipeline
import net.pooleaf.gamecore.phase.PhaseTask
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

abstract class Game {

    val gameType: Int
    var gameId: UUID? = null

    var countingStarted: Boolean = false
    var gameStarted: Boolean = false
    var pvpStarted: Boolean = false
    var ended: Boolean = false

    var startTime: LocalDateTime? = null

    var map: GameMap? = null

    var phaseTask: PhaseTask


    constructor(gameType: Int) {
        this.gameType = gameType
        this.phaseTask = PhaseTask(createPhasePipeline())
    }


    /**
     * 게임을 초기화시킵니다.
     */
    open fun init() {
        gameId = null

        countingStarted = false
        gameStarted = false
        pvpStarted = false
        ended = false

        startTime = null

        map?.unload()
        map = null

        phaseTask.cancel()
        phaseTask = PhaseTask(createPhasePipeline())

        // 플레이어 초기화
        GameCore.playerManager.getOnlinePlayers().forEach {
            it.init()
            it.joined = true

            // 오프라인일 경우 삭제
            if (!it.isOnline) {
                GameCore.playerManager.remove(it.uuid)
            }
        }
    }


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
        return !GameCore.game.countingStarted
                && GameCore.teamManager.getNotDefeatedOnlineTeams().size >= GameCore.autoGameConfig.startTeamCount
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
    fun endReset(): Boolean {
        if (!ended) return false

        // 이벤트
        onEndReset()
        Bukkit.getPluginManager().callEvent(GameEndResetEvent())

        // 스폰으로 텔레포트
        Bukkit.getOnlinePlayers().forEach { TeleportUtil.teleport(it, GameCore.spawnConfig.spawnLocation) }

        // 초기화
        init()

        // 대기 액션바
        Broadcaster.broadcastWaitingActionBar()

        // 게임 시작 조건에 충족할 경우 바로 재시작
        if (canStart()) {
            start(null)
        }

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
        GameCore.spawnConfig.spawnLocation.let {
            Bukkit.getOnlinePlayers().forEach { TeleportUtil.teleport(it, GameCore.spawnConfig.spawnLocation) }
        }

        // 초기화
        init()

        // 대기 액션바
        Broadcaster.broadcastWaitingActionBar()

        // TODO 게임 DB 저장

        return true
    }

}