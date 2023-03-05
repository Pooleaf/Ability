package net.pooleaf.ability.listeners

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.Durationable
import net.pooleaf.gamecore.events.game.GameEndEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class GameEndAbilityListener : Listener {

    @EventHandler
    fun onGameEnd(event: GameEndEvent) {
        AbilityApi.unsafe.playerManager.getPlayingPlayers().forEach { abilityPlayer ->
            val ability = abilityPlayer.ability
            if (ability == null) return@forEach

            if (ability is Cooldownable && ability.cooldownTimer.isRunning) {
                ability.cooldownTimer.cancel()
            }

            if (ability is Durationable && ability.durationTimer.isRunning) {
                ability.durationTimer.cancel()
            }
        }
    }

}