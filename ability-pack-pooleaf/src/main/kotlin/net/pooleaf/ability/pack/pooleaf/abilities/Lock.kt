package net.pooleaf.ability.pack.pooleaf.abilities

import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import net.pooleaf.gamecore.utils.toGamePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * 잠금 (아이디어: 승천)
 * 상대 타격 시 짧게 속박시킨다. (1.8 한계상 강한 둔화 + 정지로 표현)
 */
class Lock : Ability(), Listener, Cooldownable {

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "잠금"
        rank = AbilityRank.C
        type = AbilityType.PASSIVE
        description = listOf(
            "상대 타격 시 상대를 0.4초간 속박시킵니다.",
            "(아이디어: 승천)",
        )

        ban = false
    }

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 1_000L)

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onAttack(event: EntityDamageByEntityEvent) {
        if (!canUse()) return

        val abilityPlayer = abilityPlayer ?: return
        if (event.damager != abilityPlayer.player) return
        if (cooldownTimer.isRunning) return

        val target = event.entity as? Player ?: return
        val targetGamePlayer = target.toGamePlayer() ?: return
        if (abilityPlayer.team != null && abilityPlayer.team == targetGamePlayer.team) return // 아군 제외

        cooldownTimer.start()

        // 0.4초 ≈ 8틱. 강한 둔화 + 채굴/점프 억제로 사실상 속박.
        target.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 8, 6), true)
        target.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 8, 128), true) // 점프 봉쇄
        target.velocity = target.velocity.setX(0.0).setZ(0.0)
    }

}
