package net.pooleaf.gamecore.replay.listeners

import net.pooleaf.gamecore.GameCore
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class NpcHideListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        GameCore.unsafe.replayPlayerManager.values().forEach { replayPlayer ->
            replayPlayer.virtualPlayerManager.npcRegistry.forEach { npc -> event.player.hidePlayer(npc.entity as Player?) }
        }
    }

}