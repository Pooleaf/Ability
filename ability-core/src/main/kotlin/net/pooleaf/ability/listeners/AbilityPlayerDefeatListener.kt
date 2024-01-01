package net.pooleaf.ability.listeners

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.gamecore.events.player.GamePlayerDeathEvent
import net.pooleaf.gamecore.events.player.GamePlayerDefeatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityPlayerDefeatListener: Listener {

    @EventHandler
    fun onDeath(event: GamePlayerDeathEvent) {
        BukkitSyncScope.launch {
            if (!AbilityApi.game.isGameStarted) return@launch

            val gamePlayer = event.deadGamePlayer
            if (gamePlayer.isDefeated) return@launch

            gamePlayer.defeat()
            gamePlayer.sendTitleSafely("§c탈락했습니다")
            gamePlayer.playSoundSafely(XSound.ENTITY_WITHER_DEATH, 0.5F, 1.0F)
        }
    }

    @EventHandler
    fun onDefeat(event: GamePlayerDefeatEvent) {
        val gamePlayer = event.gamePlayer as AbilityPlayer

        BukkitSyncScope.launch {
            try {
                // 탈락 시 능력 할당 해제
                gamePlayer.resignAbility()
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

}