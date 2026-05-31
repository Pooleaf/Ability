package net.pooleaf.ability.pack.pooleaf.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask

class Warden : Ability(), Listener {

    private var blindnessTask: BukkitTask? = null

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "워든"
        rank = AbilityRank.HIDDEN
        type = AbilityType.PASSIVE
        description = listOf(
            "실명 상태가 유지됩니다.",
            "대신 당신의 공격은 상대의 방어력을 무시합니다.",
        )

        ban = false
    }

    override fun onAssign() {
        giveBlindness()
        blindnessTask = Bukkit.getScheduler().runTaskTimer(PooleafAbilityPlugin.instance, {
            giveBlindness()
        }, 20L, 20L)
    }

    override fun onResign() {
        blindnessTask?.cancel()
        blindnessTask = null
        abilityPlayer?.player?.removePotionEffect(PotionEffectType.BLINDNESS)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player != event.damager) return
        if (event.entity !is LivingEntity) return
        if (!event.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) return

        event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0)
    }

    private fun giveBlindness() {
        abilityPlayer?.player?.addPotionEffect(
            PotionEffect(PotionEffectType.BLINDNESS, Int.MAX_VALUE, 0),
            true
        )
    }

}
