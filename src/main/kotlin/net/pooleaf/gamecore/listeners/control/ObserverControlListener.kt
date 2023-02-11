package net.pooleaf.gamecore.listeners.control

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.EntityDamageByPlayerEvent
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageByPlayerEvent
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.player.GamePlayerJoinEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*

/**
 * 관전 모드 플레이어를 컨트롤하는 Listener
 */
class ObserverControlListener: Listener {

    private fun isObserver(player: Player): Boolean {
        val gamePlayer = GameCore.unsafe.playerManager.get(player.uniqueId)

        return gamePlayer?.isSpectator == true
    }

    private fun isWaiting(): Boolean {
        return !GameCore.game.isTeleportedToMap
    }


    @EventHandler
    fun onPlayerJoin(event: GamePlayerJoinEvent) {
        // 접속자가 관전자 안보이게하기
        GameCore.unsafe.playerManager.getOnlineObservers().forEach { event.gamePlayer.player.hidePlayer(it.player) }
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
    fun onHit(event: EntityDamageByPlayerEvent) {
        if (isObserver(event.damager)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPvp(event: PlayerDamageByPlayerEvent) {
        if (isObserver(event.player) || isObserver(event.damager)) {
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

    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        if (isObserver(event.player) && !isWaiting()) {
            event.player.allowFlight = true
            event.player.isFlying = true
        }
    }

}