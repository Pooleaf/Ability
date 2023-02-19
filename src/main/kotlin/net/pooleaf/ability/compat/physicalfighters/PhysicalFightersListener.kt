package net.pooleaf.ability.compat.physicalfighters

import Physical.Fighters.AbilityList.Time
import Physical.Fighters.MainModule.AbilityBase
import Physical.Fighters.MainModule.EventManager
import Physical.Fighters.MinerModule.EventData
import net.pooleaf.ability.AbilityApi
import net.pooleaf.core.modules.support.common.AutoRegisterExclude
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.*


@AutoRegisterExclude
class PhysicalFightersListener: Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        excuteAbility(event, EventManager.onEntityDamage)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        excuteAbility(event, EventManager.onEntityDamageByEntity)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityTarget(event: EntityTargetEvent) {
        excuteAbility(event, EventManager.onEntityTarget)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        excuteAbility(event, EventManager.onFoodLevelChange)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityRegainHealth(event: EntityRegainHealthEvent) {
        excuteAbility(event, EventManager.onEntityRegainHealth)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        excuteAbility(event, EventManager.onBlockPlaceEvent)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        excuteAbility(event, EventManager.onBlockBreakEvent)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onSignChange(event: SignChangeEvent) {
        excuteAbility(event, EventManager.onSignChangeEvent)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerToggleSneak(event: PlayerToggleSneakEvent) {
        excuteAbility(event, EventManager.onPlayerToggleSneakEvent)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        excuteAbility(event, EventManager.onProjectileLaunchEvent)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        excuteAbility(event, EventManager.onPlayerPickupItem)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        excuteAbility(event, EventManager.onPlayerRespawn)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDeath(event: EntityDeathEvent) {
        excuteAbility(event, EventManager.onEntityDeath)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        excuteAbility(event)
        excuteAbility(event, EventManager.onPlayerInteract)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        excuteAbility(event, EventManager.onPlayerMoveEvent)
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onProjectileHit(event: ProjectileHitEvent) {
        excuteAbility(event, EventManager.onProjectileHitEvent)
    }

    fun excuteAbility(event: Event, eventDatas: List<EventData>) {
        if (!AbilityApi.unsafe.abilityService.canUseAbility()) return

        var b = false
        for (eventData in eventDatas) {
            for (ability in getAbilities(eventData.ab)) {
                if (ability.cooldownMillis > 0 && ability.durationMillis > 0) {
                    if (eventData.ab.GetPlayer() != null && ability.remainingDurationMillis > 0) {
                        if (ability.originalAbility is Time) {
                            val e = event as PlayerMoveEvent
                            if (ability.player?.player != e.player) {
                                e.to = e.from
                            }
                        } else {
                            eventData.ab.A_Effect(event, 0)
                        }
                        b = true
                        continue
                    }
                } else if (ability.excute(event, eventData.parameter)) {
                    b = true
                    continue
                }
            }
            if (b) return
        }
    }

    fun excuteAbility(event: PlayerInteractEvent) {
        if (!AbilityApi.unsafe.abilityService.canUseAbility()) return

        if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
            for (abilityBase in EventManager.LeftHandEvent) {
                getAbilities(abilityBase).forEach { it.excute(event, 0) }
            }
        } else if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
            for (abilityBase in EventManager.RightHandEvent) {
                getAbilities(abilityBase).forEach { it.excute(event, 1) }
            }
        }
    }

    private fun getAbilities(ab: AbilityBase): List<PhysicalFightersCompatAbility> {
        return AbilityApi.unsafe.abilityManager.getAssignedAbilities()
            .filter { it is PhysicalFightersCompatAbility }
            .filter { it.name == ab.GetAbilityName() }
            .map { it as PhysicalFightersCompatAbility }
            .toList()
    }
    
}