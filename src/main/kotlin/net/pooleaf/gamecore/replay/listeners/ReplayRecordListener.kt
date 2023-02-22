package net.pooleaf.gamecore.replay.listeners

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.*
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerMoveEvent

class ReplayRecordListener : Listener {

    private fun isRecording(): Boolean {
        return GameCore.unsafe.recordManager.isRecording()
    }

    private fun isRecordingTargetPlayer(player: Player): Boolean {
        return GameCore.unsafe.recordManager.record!!.recordTargetPlayer.contains(player.uniqueId)
    }



}