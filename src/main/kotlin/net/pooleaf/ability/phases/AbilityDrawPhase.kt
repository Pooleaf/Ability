package net.pooleaf.ability.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityApi.playerManager
import net.pooleaf.ability.ability.drawer.AbilityDrawer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.phase.Phase

class AbilityDrawPhase: Phase() {

    override fun onStart() {
        Broadcaster.broadcastActionBar("§c잠시 후 능력 추첨이 시작됩니다.")
        Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3f, 0.7f)
    }

    override fun onRun() {
        if (count == 4) {
            // 추첨할 능력이 없을 경우 게임 중단
            if (AbilityApi.abilityManager.abilities.isEmpty()) {
                BukkitAsyncScope.launch { AbilityApi.game.cancel() }
            }

            // 능력 추첨 시작
            AbilityApi.game.abilityDrawStarted = true

            val drawer = AbilityDrawer(AbilityApi.abilityManager.abilities, true, 12)
            AbilityApi.playerManager.getJoinedPlayers().forEach { abilityPlayer ->
                BukkitAsyncScope.launch {
                    val ability = drawer.drawTo(abilityPlayer)

                    delay(2000L)
                    abilityPlayer.player?.let { player ->
                        ability?.sendManual(player) ?: player.sendMessage("능력이 부족하여 능력을 할당받지 못했습니다.")
                        XSound.ENTITY_ITEM_PICKUP.play(player)

                        if (ability != null) {
                            player.sendMessage("")

                            delay(1000L)
                            val yesComponent = SimpleComponentBuilder("§2§l[ 능력 확정 ]")
                                .clickRunCommand("/확정")
                                .hoverShowText("§e클릭 시 §f${ability.name} ${ability.rank.color}(${ability.rank.name}) §e능력으로 확정합니다. §7(/확정)")
                                .build()
                            val noComponent = SimpleComponentBuilder(" §c§l[ 다시 뽑기 ]") // TODO 추첨 횟수 체크해서 없으면 비활성화 (회색)
                                .clickRunCommand("/다시뽑기")
                                .hoverShowText("§e클릭 시 능력을 다시 뽑습니다. §7(/다시뽑기)")
                                .build()
                            yesComponent.addExtra(noComponent)

                            player.sendMessage(yesComponent)
                            XSound.ENTITY_GENERIC_EAT.play(player, 0.5F, 1F)
                        }
                    }
                }
            }
        }
    }

    override fun onEnd() {
        Broadcaster.broadcastActionBar("§e모든 플레이어가 능력을 확정했습니다.")
        Broadcaster.broadcastSound(XSound.ENTITY_PLAYER_LEVELUP, 1f, 1f)

        Logger.nlog("§e[ 플레이어 능력 ]")
        for (joinedPlayer in playerManager.getJoinedPlayers()) {
            val abilityName = joinedPlayer.ability?.name ?: "없음"
            Logger.nlog("§e${joinedPlayer.name}: §f${abilityName}")
        }
    }

}