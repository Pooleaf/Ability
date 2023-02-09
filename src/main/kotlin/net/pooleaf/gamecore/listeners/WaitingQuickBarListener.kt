package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class WaitingQuickBarListener: Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun onJoin(event: PlayerJoinEvent) {
        if (GameCore.game.gameStarted) return

        val player = event.player
        val gamePlayer = GameCore.playerManager.get(player.uniqueId)

        if (!gamePlayer.observer) {
            GameCore.quickBarManager.waitingQuickBar.setTo(player)
        }

        GameCore.mapVoteService.mapVoteGui.updateAsynchronously()
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onQuit(event: PlayerQuitEvent) {
        GameCore.startVoteService.startVote.unvote(event.player.uniqueId)
        GameCore.startVoteService.startVoteGui.updateAsynchronously()

        GameCore.mapVoteService.mapVote.unvote(event.player.uniqueId)
        GameCore.mapVoteService.mapVoteGui.updateAsynchronously()
    }

}