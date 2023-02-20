package net.pooleaf.ability.listeners

import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.gamecore.events.player.GamePlayerJoinEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityAssignReserveListener: Listener {

    @EventHandler
    fun onJoin(event: GamePlayerJoinEvent) {
        val abilityPlayer = event.gamePlayer as AbilityPlayer

        if (abilityPlayer.isPlaying() && abilityPlayer.isAbilityAssignReserved && abilityPlayer.tempAbility != null) {
            abilityPlayer.assignAbility(abilityPlayer.tempAbility!!)

            abilityPlayer.ability?.sendManual(abilityPlayer.player)
            abilityPlayer.playSoundSafely(XSound.ENTITY_ITEM_PICKUP, 0.4F, 1.0F)
        }
    }

}