package net.pooleaf.gamecore.v1.listeners

import net.pooleaf.gamecore.v1.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class WaitingQuickBarListener: Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onJoin(event: PlayerJoinEvent) {
        if (GameCore.game.isGameStarted) return

        val player = event.player
        val gamePlayer = GameCore.playerManager.get(player.uniqueId)

        if (!gamePlayer.observer) {
            GameCore.quickBarManager.waitingQuickBar.setTo(player)
        }

        GameCore.mapVoteManager.mapVoteGui.updateAsynchronously()
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onQuit(event: PlayerQuitEvent) {
        GameCore.startVoteManager.startVote.unvote(event.player.uniqueId)
        GameCore.startVoteManager.startVoteGui.updateAsynchronously()

        GameCore.mapVoteManager.mapVote.unvote(event.player.uniqueId)
        GameCore.mapVoteManager.mapVoteGui.updateAsynchronously()
    }

}