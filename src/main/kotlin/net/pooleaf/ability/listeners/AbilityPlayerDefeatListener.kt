package net.pooleaf.ability.listeners

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.gamecore.events.player.GamePlayerDeathEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityPlayerDefeatListener: Listener {

    @EventHandler
    fun onDeath(event: GamePlayerDeathEvent) {
        BukkitSyncScope.launch {
            if (!AbilityApi.game.isGameStarted) return@launch

            val gamePlayer = event.deadGamePlayer
            gamePlayer.defeat()
            gamePlayer.sendTitleSafely("§c탈락했습니다")
            gamePlayer.playSoundSafely(XSound.ENTITY_WITHER_DEATH, 0.5F, 1.0F)
        }
    }

}