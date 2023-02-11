package net.pooleaf.ability.listeners

import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.gamecore.events.player.GamePlayerInitEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityPlayerListener: Listener {

    @EventHandler
    fun onPlayerInit(event: net.pooleaf.gamecore.events.player.GamePlayerInitEvent) {
        val abilityPlayer = event.gamePlayer as AbilityPlayer

        abilityPlayer.resignAbility()
        abilityPlayer.tempAbility = null

        abilityPlayer.redrawCount = 0
        abilityPlayer.maxRedrawCount = 1 // TODO 능력 재추첨 횟수 불러오기
        abilityPlayer.abilityDrawComplete = false
    }

}