package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class WaitingQuickBarListener: Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (GameCore.game.gameStarted) return

        val player = event.player
        val gamePlayer = GameCore.playerManager.get(player.uniqueId)

        if (!gamePlayer.observer) {
            GameCore.quickBarManager.waitingQuickBar.setTo(player)
        }
    }

}