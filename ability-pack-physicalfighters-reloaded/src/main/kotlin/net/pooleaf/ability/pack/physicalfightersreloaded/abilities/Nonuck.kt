package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.gamecore.utils.damageBypassAntiCheat
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class Nonuck : Ability(), Listener {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "무통증"
        rank = AbilityRank.A
        type = AbilityType.PASSIVE
        description = listOf(
            "공격당할 때 80% 확률로 넉백을 무시합니다."
        )

        ban = false
    }


    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player != event.entity) return

        if (event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.cause == EntityDamageEvent.DamageCause.PROJECTILE) {
            val player = event.entity as Player
            val damager = if (event.damager is Projectile) {
                (event.damager as Projectile).shooter
            } else {
                event.damager
            }

            if (damager is Player) {
                player.damageBypassAntiCheat(event.damage, damager)
            } else {
                player.damage(event.damage)
            }

            event.isCancelled = true
        }
    }

}