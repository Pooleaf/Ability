package net.pooleaf.abilityreward.listeners

import net.pooleaf.abilityreward.AbilityRewardApi
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameEndEvent
import net.pooleaf.gamecore.events.player.GamePlayerDefeatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityRewardListener : Listener {

    @EventHandler
    fun onKill(event: GamePlayerDefeatEvent) {
        if (event.killerGamePlayer == null) return

        // 킬
        AbilityRewardApi.unsafe.abilityRewardService.giveKillMoney(event.killerGamePlayer!!)

        // 연속킬
        if (GameCore.gameConfig.useKillStreak) {
            AbilityRewardApi.unsafe.abilityRewardService.giveKillStreakMoney(event.killerGamePlayer!!)
        }

        // 어시스트
        event.assistGamePlayers?.forEach { assistPlayer ->
            AbilityRewardApi.unsafe.abilityRewardService.giveAssistMoney(assistPlayer)
        }
    }

    @EventHandler
    fun onWin(event: GameEndEvent) {
        if (event.winnerTeam == null) return;

        AbilityRewardApi.unsafe.abilityRewardService.giveWinMoney(event.winnerTeam!!)
    }

}