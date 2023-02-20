package net.pooleaf.ability.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase
import net.pooleaf.gamecore.utils.StringUtil

class AbilityDrawPhase: Phase() {

    var skipJob: Job? = null
    var skipRemainingSeconds: Int? = null


    override fun onInit() {
        skipJob?.cancel()
        skipJob = null

        skipRemainingSeconds = null
    }

    override suspend fun onStart() {
        Broadcaster.broadcastActionBar("§e잠시 후 능력 추첨이 시작됩니다.")
        Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)

        delay(4000L)

        // 추첨할 능력이 없을 경우 게임 중단
        if (AbilityApi.unsafe.abilityManager.abilities.isEmpty()) {
            BukkitAsyncScope.launch { AbilityApi.game.cancel(null, "능력이 부족하여 게임을 시작할 수 없습니다.") }
        }

        // 능력 추첨 시작
        AbilityApi.game.abilityDrawStarted = true

        // 모든 플레이어 능력 추첨
        // 능력이 부족할 때 능력을 할당 받는 플레이어를 일정하게 하지 않게 위해 플레이어 리스트를 섞음
        AbilityApi.unsafe.playerManager.getJoinedPlayers().shuffled().forEach { abilityPlayer ->
            abilityPlayer.abilityDrawJob = BukkitAsyncScope.launch {
                AbilityApi.unsafe.abilityService.drawAbility(abilityPlayer)
            }
        }

        // 강제 확정 타이머
        skipJob = BukkitAsyncScope.launch {
            val drawSkipSeconds = AbilityApi.abilityGameConfig.drawSkipSeconds

            for (count in drawSkipSeconds!! downTo 1) {
                skipRemainingSeconds = count

                if (count <= 10) {
                    val remainingTime = StringUtil.buildTimeStringWithColor(count * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

                    Broadcaster.broadcast("${remainingTime} §e후 능력이 강제로 확정됩니다.")
                    Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
                }

                GameCore.unsafe.sideBarManager.sideBar?.update()

                delay(1000L)
            }

            AbilityApi.unsafe.abilityService.skipAbilityDraw()

            Broadcaster.broadcast("§e모든 플레이어의 능력이 강제로 확정되었습니다.")
            Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
        }
    }

    override suspend fun onRun() {
        // 모든 플레이어가 능력을 확정하면 Phase 종료
        while (!AbilityApi.unsafe.playerManager.getOnlineJoinedPlayers().all { it.abilityDrawComplete }) {
            delay(100L)
        }
    }

    override fun onEnd() {
        Broadcaster.broadcastActionBar("§e모든 플레이어가 능력을 확정했습니다.")
        Broadcaster.broadcastSound(XSound.ENTITY_PLAYER_LEVELUP, 0.4F, 1.0F)

        Logger.nlog("§e[ 플레이어 능력 ]")
        AbilityApi.unsafe.playerManager.getJoinedPlayers().forEach {abilityPlayer ->
            val abilityName = abilityPlayer.ability?.name ?: "없음"
            Logger.nlog("§e${abilityPlayer.name}(${abilityPlayer.uuid}): §f${abilityName}")
        }

        onInit()
    }

    override fun onCancel() {
        onInit()
    }

}