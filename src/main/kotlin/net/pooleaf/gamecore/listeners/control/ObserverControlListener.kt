package net.pooleaf.gamecore.listeners.control

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageByPlayerEvent
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamecore.GameCore
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*

/**
 * 관전 모드 플레이어를 컨트롤하는 Listener
 */
class ObserverControlListener {

    private fun isObserver(player: Player): Boolean {
        val gamePlayer = GameCore.playerManager.get(player.uniqueId)
        return gamePlayer.observer || gamePlayer.defeated
    }

    private fun isWaiting(): Boolean {
        return !GameCore.game.gameStarted
    }


    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (isObserver(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (isObserver(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBucketFill(event: PlayerBucketFillEvent) {
        if (isObserver(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        if (isObserver(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDamage(event: PlayerDamageEvent) {
        if (isObserver(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPvp(event: PlayerDamageByPlayerEvent) {
        if (isObserver(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (isObserver(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        if (isObserver(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPickupItem(event: PlayerPickupItemEvent) {
        if (isObserver(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        if (isObserver(event.entity)) {
            event.drops.clear()
            event.droppedExp = 0
        }
    }

    @EventHandler
    fun onFly(event: PlayerToggleFlightEvent) {
        if (isObserver(event.player) && isWaiting() && !event.player.isOp) {
            event.player.isFlying = false
            event.isCancelled = true
        }
    }

}