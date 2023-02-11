package net.pooleaf.gamecore.listeners

import net.pooleaf.gamecore.GameCore
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class VoteListener: Listener {

    @EventHandler
    fun onPlayerQuit(event: net.pooleaf.gamecore.events.player.GamePlayerQuitEvent) {
        val gamePlayer = event.gamePlayer

        GameCore.unsafe.startVoteManager.unvote(gamePlayer)
        GameCore.unsafe.mapVoteManager.unvote(gamePlayer)
    }

}