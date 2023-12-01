package net.pooleaf.abilityreward.listeners

import net.pooleaf.abilityreward.AbilityRewardApi
import net.pooleaf.gamecore.events.game.GameEndEvent
import net.pooleaf.gamecore.events.player.GamePlayerDefeatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityRewardListener : Listener {

    @EventHandler
    fun onKill(event: GamePlayerDefeatEvent) {
        if (event.killerGamePlayer == null) return

        AbilityRewardApi.unsafe.abilityRewardService.giveKillMoney(event.killerGamePlayer!!)
    }

    @EventHandler
    fun onWin(event: GameEndEvent) {
        if (event.winnerTeam == null) return;

        AbilityRewardApi.unsafe.abilityRewardService.giveWinMoney(event.winnerTeam!!)
    }

}