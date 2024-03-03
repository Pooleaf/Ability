package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.*
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.gamecore.utils.damageBypassAntiCheat
import net.pooleaf.gamecore.utils.toGamePlayer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack

/**
 * 공격 핵방지 우회
 */
class Angle : Ability(), Listener, Cooldownable, Durationable {

    var targetPlayer: Player? = null

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "천사"
        rank = AbilityRank.SS
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴로 공격한 대상에게 10초간 자신이 받는 데미지의 반을 흡수시킵니다.",
            "독, 질식, 낙하 데미지를 받지 않습니다.",
        )

        ban = false
    }

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 80_000L)
    override val durationTimer: DurationTimer = object : DurationTimer(this, 10_000L) {
        override fun onEnd() {
            super.onEnd()

            targetPlayer?.sendMessage("§a이제 ${abilityPlayer?.displayName} §a님의 데미지를 흡수하지 않습니다.")
            targetPlayer = null
        }
    }


    @EventHandler
    fun setTarget(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) return
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (cooldownTimer.remainingTimeMillis != 0L || durationTimer.isRunning) return

        val damager = event.damager
        val damaged = event.entity

        if (damager !is Player || damaged !is Player || damager != abilityPlayer?.player) return
        if (damager.itemInHand?.isSimilar(ItemStack(Material.IRON_INGOT)) != true) return
        if (damaged.toGamePlayer()?.isPlaying() != true) return

        targetPlayer = damaged

        cooldownTimer.start()
        durationTimer.start()

        abilityPlayer?.sendMessageSafely("${targetPlayer!!.toGamePlayer()?.displayName} §e님께서 이제 §f10§e초간 당신의 데미지 반을 흡수합니다.")
        targetPlayer!!.sendMessage("§c당신은 §f10§c초간 §f${abilityPlayer?.displayName} §c님이 받는 데미지의 반을 흡수합니다.")
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun absorbDamage(event: EntityDamageEvent) {
        if (event.isCancelled) return
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player != event.entity) return

        val targetPlayer = targetPlayer
        if (!durationTimer.isRunning || targetPlayer == null || !targetPlayer.isOnline) return

        targetPlayer.damageBypassAntiCheat(event.damage / 2, abilityPlayer?.player!!)
        event.damage /= 2
    }

    @EventHandler
    fun cancelDamage(event: EntityDamageEvent) {
        if (event.isCancelled) return
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player != event.entity) return

        if (event.cause == EntityDamageEvent.DamageCause.POISON
            || event.cause == EntityDamageEvent.DamageCause.DROWNING
            || event.cause == EntityDamageEvent.DamageCause.FALL) event.isCancelled = true
    }

}