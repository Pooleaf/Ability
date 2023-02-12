package net.pooleaf.ability.game

import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.PhasePipeline
import net.pooleaf.gamecore.phases.*
import org.bukkit.GameMode

class AbilityPhasePipeline: PhasePipeline() {

    init {
        // 시작 카운트
        addPhase(StartCountPhase(true))
        addPhase(DelayPhase(2))

        // 게임모드 변경
        addPhase(RunnablePhase() {
            BukkitSyncScope.launch { AbilityApi.game.changeCurrentGameMode(GameMode.SURVIVAL) }
        })

        // 능력 추첨
        addPhase(AbilityDrawPhase())

        // PVP 시작
        addPhase(RunnablePhase() {
            AbilityApi.game.isGodMode = true
        })

        // 경계선 줄이기 (1차)
        addPhase(object : WorldBorderUpdatePhase() {
            override fun getNewWorldBorderSize(): Int = (GameCore.currentMap!!.worldBorderSize * 2.toFloat() / 3).toInt()
            override fun getUpdateWaitSeconds(): Int = 60 * 6
            override fun getUpdateSizePerSeconds(): Int = 5
        })

        // 경계선 줄이기 (2차)
        addPhase(object : WorldBorderUpdatePhase() {
            override fun getNewWorldBorderSize(): Int = (GameCore.currentMap!!.worldBorderSize * 1.toFloat() / 3).toInt()
            override fun getUpdateWaitSeconds(): Int = 60 * 3
            override fun getUpdateSizePerSeconds(): Int = 5
        })

        // 경계선 줄이기 (3차) - 마지막 크기는 20칸으로 고정
        addPhase(object : WorldBorderUpdatePhase() {
            override fun getNewWorldBorderSize(): Int = 20
            override fun getUpdateWaitSeconds(): Int = 60 * 3
            override fun getUpdateSizePerSeconds(): Int = 5
            override suspend fun onStart() {
                // 이미 2차에서 20칸보다 작아졌을 경우 스킵
                if (GameCore.currentMap!!.currentWorldBorderSize <= getNewWorldBorderSize()) return

                super.onStart()
            }
        })

        addPhase(GamePhase())

        // 종료
        addPhase(EndPhase())
    }

}