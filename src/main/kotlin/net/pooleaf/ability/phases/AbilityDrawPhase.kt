package net.pooleaf.ability.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.phase.Phase

class AbilityDrawPhase: Phase() {

    override fun onStart() {
        Broadcaster.broadcastActionBar("§c잠시 후 능력 추첨이 시작됩니다.")
        Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3f, 0.7f)
    }

    override fun onRun() {
        // 4초 뒤 능력 추첨
        if (count == 4) {
            // 추첨할 능력이 없을 경우 게임 중단
            if (AbilityApi.abilityManager.abilities.isEmpty()) {
                BukkitAsyncScope.launch { AbilityApi.game.cancel() }
            }

            // 능력 추첨 시작
            AbilityApi.game.abilityDrawStarted = true

            // 모든 플레이어 능력 추첨
            AbilityApi.playerManager.getJoinedPlayers().forEach { abilityPlayer ->
                BukkitAsyncScope.launch {
                    val tempAbility = AbilityApi.abilityManager.getRandomAbilityNoDuplicatedInTemp()
                    AbilityApi.abilityDrawer.drawWithYesNoMessage(abilityPlayer, AbilityApi.abilityManager.abilities, tempAbility)
                }
            }
        }

        // 모든 플레이어가 능력을 확정하면 Phase 종료
        if (AbilityApi.playerManager.getOnlineJoinedPlayers().all { it.abilityDrawComplete }) {
            end()
        }
    }

    override fun onEnd() {
        Broadcaster.broadcastActionBar("§e모든 플레이어가 능력을 확정했습니다.")
        Broadcaster.broadcastSound(XSound.ENTITY_PLAYER_LEVELUP, 1F, 1F)

        Logger.nlog("§e[ 플레이어 능력 ]")
        AbilityApi.playerManager.getJoinedPlayers().forEach {abilityPlayer ->
            val abilityName = abilityPlayer.ability?.name ?: "없음"
            Logger.nlog("§e${abilityPlayer.name}(${abilityPlayer.uuid}): §f${abilityName}")
        }
    }

}