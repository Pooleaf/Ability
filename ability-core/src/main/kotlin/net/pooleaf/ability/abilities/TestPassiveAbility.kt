package net.pooleaf.ability.abilities

import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageByEntityEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class TestPassiveAbility: Ability(), Cooldownable, Listener {

    init {
        pluginName = AbilityPlugin.instance.name

        name = "테스트 패시브"
        rank = AbilityRank.C
        type = AbilityType.PASSIVE
        description = listOf("공격을 받으면 상대에게 번개가 칩니다.")

        ban = true
    }

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 3000L)

    @EventHandler
    fun onPlayerDamageByEntity(event: PlayerDamageByEntityEvent) {
        if (!canUse()) return

        abilityPlayer?.player?.let { player ->
            // 플레이어 체크
            if (!event.player.equals(player)) return

            // 쿨타임 체크
            if (remainingCooldownMillis > 0) return

            cooldownTimer.start()
            event.damager.world.strikeLightning(event.damager.location)
        }
    }

}