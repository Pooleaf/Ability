package net.pooleaf.gamecore.v2.listeners

import net.pooleaf.gamecore.v2.GameCore
import net.pooleaf.gamecore.v2.events.player.GamePlayerQuitEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class VoteListener: Listener {

    @EventHandler
    fun onPlayerQuit(event: GamePlayerQuitEvent) {
        val gamePlayer = event.gamePlayer

        GameCore.unsafe.startVoteManager.unvote(gamePlayer)
        GameCore.unsafe.mapVoteManager.unvote(gamePlayer)
    }

}