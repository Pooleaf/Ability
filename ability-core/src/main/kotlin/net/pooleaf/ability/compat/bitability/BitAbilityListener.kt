package net.pooleaf.ability.compat.bitability

import net.pooleaf.ability.AbilityApi
import net.pooleaf.core.modules.support.common.AutoRegisterExclude
import org.bukkit.entity.*
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerRespawnEvent


@AutoRegisterExclude
class BitAbilityListener: Listener {

    fun isBitAbilityPlayer(entity: Entity): Boolean {
        if (entity !is Player) return false

        val gamePlayer = AbilityApi.unsafe.playerManager.get(entity.uniqueId)
        return gamePlayer.ability is BitCompatAbility
    }

    fun excuteAbility(event: Event, player: Player, data: Int) {
        if (!AbilityApi.unsafe.abilityService.canUseAbility()) return
        val gamePlayer = AbilityApi.unsafe.playerManager.get(player.uniqueId)
        gamePlayer.ability?.let { ability ->
            if (ability !is BitCompatAbility) return@let
            ability.excute(event, data)
        }
    }

    fun excuteAbility(event: Event, entity: Entity, data: Int) {
        if (entity !is Player) return
        excuteAbility(event, entity, data)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDamage(event: EntityDamageEvent) {
        excuteAbility(event, event.entity, 0)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.cause == EntityDamageEvent.DamageCause.THORNS) return

        if (event.damager is Arrow && (event.damager as Projectile).shooter is Player) {
            excuteAbility(event, event.entity, 93)
            excuteAbility(event, (event.damager as Arrow).shooter as Player, 92)
        } else if (event.damager is Snowball && (event.damager as Projectile).shooter is Player) {
            excuteAbility(event, event.entity, 95)
            excuteAbility(event, (event.damager as Snowball).shooter as Player, 94)
        } else if (event.damager is Fireball && (event.damager as Projectile).shooter is Player) {
            excuteAbility(event, event.entity, 97)
            excuteAbility(event, (event.damager as Fireball).shooter as Player, 96)
        } else if (event.entity is Player) {
            val player = event.entity as Player
            if (player.noDamageTicks.toFloat() <= player.maximumNoDamageTicks.toFloat() / 2.0F) {
                excuteAbility(event, event.damager, 9)
            } else {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityTarget(event: EntityTargetEvent) {
        excuteAbility(event, event.entity, 0)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onTask(event: InventoryClickEvent) {
        if (event.getSlotType() !== InventoryType.SlotType.ARMOR) return
        excuteAbility(event, event.whoClicked, 0)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        // 배고픔 무한 모드일 경우 return 이므로 주석 처리
        // excuteAbility(event, event.entity, 0)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityRegainHealth(event: EntityRegainHealthEvent) {
        excuteAbility(event, event.entity, 0)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        excuteAbility(event, event.player, 0)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBucketEmpty(event: PlayerBucketEmptyEvent) {
        excuteAbility(event, event.player, 0)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        excuteAbility(event, event.entity, 0)
        if (event.entity.killer != null && event.entity.killer is Player) {
            excuteAbility(event, event.entity.killer, 1)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        excuteAbility(event, event.player, 0)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        excuteAbility(event, event.player, 0)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onProjectileHit(event: ProjectileHitEvent) {
        if (event.entity.shooter is Player && !isBitAbilityPlayer(event.entity.shooter as Player)) return

        if (event.entityType == EntityType.SNOWBALL) {
            event.entity.remove()
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        if (event.entity.shooter !is Player) return
        if (event.entityType === EntityType.SNOWBALL) {
            excuteAbility(event, event.entity.shooter as Player, 0)
        } else if (event.entityType === EntityType.ARROW) {
            excuteAbility(event, event.entity.shooter as Player, 1)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onSignChange(event: SignChangeEvent) {
        excuteAbility(event, event.player, 0)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockPlace(event: BlockPlaceEvent) {
        excuteAbility(event, event.player, 0)
    }

}