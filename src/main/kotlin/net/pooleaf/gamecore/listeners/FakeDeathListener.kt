package net.pooleaf.gamecore.listeners

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerDeathEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class FakeDeathListener: Listener {


    @EventHandler
    fun onDeathDamage(event: PlayerDamageEvent) {
        val player = event.player

        // 죽을 만큼 데미지를 받을 경우
        if (player.health - event.entityDamageEvent.finalDamage < 1) {
            val deadGamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)

            val lastAttackerInfo = deadGamePlayer.lastDamagerInfo
            val killerGamePlayer = if (lastAttackerInfo?.let { System.currentTimeMillis() - it.second < GameCore.gameConfig.killValidSeconds * 1000L } == true) {
                lastAttackerInfo.first
            } else {
                null
            }

            // 이벤트 캔슬
            event.isCancelled = true

            // 이벤트
            Bukkit.getPluginManager().callEvent(GamePlayerDeathEvent(deadGamePlayer, killerGamePlayer, event))
        }
    }

}