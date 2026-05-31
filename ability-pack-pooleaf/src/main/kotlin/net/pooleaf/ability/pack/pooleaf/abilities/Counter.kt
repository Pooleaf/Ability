package net.pooleaf.ability.pack.pooleaf.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import net.pooleaf.gamecore.utils.damageBypassAntiCheat
import net.pooleaf.gamecore.utils.toGamePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * 카운터 (아이디어: 승천)
 * 자신에게 공격받은 사람은 잠깐 크게 느려지며, 10% 확률로 추가 피해를 받는다.
 */
class Counter : Ability(), Listener {

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "카운터"
        rank = AbilityRank.B
        type = AbilityType.PASSIVE
        description = listOf(
            "공격한 상대를 0.15초간 크게 둔화시킵니다.",
            "10% 확률로 3의 추가 피해를 줍니다.",
            "(아이디어: 승천)",
        )

        ban = false
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onAttack(event: EntityDamageByEntityEvent) {
        if (!canUse()) return

        val abilityPlayer = abilityPlayer ?: return
        if (event.damager != abilityPlayer.player) return

        val target = event.entity as? Player ?: return
        val targetGamePlayer = target.toGamePlayer() ?: return
        if (abilityPlayer.team != null && abilityPlayer.team == targetGamePlayer.team) return // 아군 제외

        // 0.15초 ≈ 3틱 동안 강한 둔화.
        target.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 3, 4), true)

        if (Math.random() < 0.10) {
            target.damageBypassAntiCheat(3.0, abilityPlayer.player)
        }
    }

}
