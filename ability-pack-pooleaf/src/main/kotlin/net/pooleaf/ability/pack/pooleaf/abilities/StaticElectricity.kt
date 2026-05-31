package net.pooleaf.ability.pack.pooleaf.abilities

import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import net.pooleaf.gamecore.utils.damageBypassAntiCheat
import net.pooleaf.gamecore.utils.toGamePlayer
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class StaticElectricity : Ability(), Listener, Cooldownable {

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "정전기"
        rank = AbilityRank.B
        type = AbilityType.PASSIVE
        description = listOf(
            "공격을 하거나 공격을 받으면 서로에게 2 데미지를 줍니다.",
            "같은 팀 플레이어에게는 발동하지 않습니다.",
        )

        ban = false
    }

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 3_000L)

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isEnded || AbilityApi.game.isGodMode) return

        val abilityPlayer = abilityPlayer ?: return
        val owner = abilityPlayer.player ?: return

        // 정전기로 준 데미지가 다시 이 핸들러를 트리거하는 것을 막는다.
        if (cooldownTimer.isRunning) return

        val damager = (event.damager as? Projectile)?.shooter ?: event.damager

        // 내가 공격자거나 피격자일 때만 발동하고, 상대를 찾는다.
        val opponent: Player = when {
            event.entity == owner -> damager as? Player ?: return
            damager == owner -> event.entity as? Player ?: return
            else -> return
        }

        if (opponent == owner) return

        // 아군 제외 (무소속끼리는 서로 적으로 본다)
        val opponentGamePlayer = opponent.toGamePlayer()
        if (abilityPlayer.team != null && abilityPlayer.team == opponentGamePlayer?.team) return

        // 데미지 부여 전에 쿨타임을 돌려 재진입(무한 루프)을 막는다.
        cooldownTimer.start()

        owner.damageBypassAntiCheat(DAMAGE, opponent)
        opponent.damageBypassAntiCheat(DAMAGE, owner)

        playEffect(owner)
        playEffect(opponent)
    }

    private fun playEffect(player: Player) {
        player.world.playSound(player.location, XSound.ENTITY_CREEPER_HURT.parseSound(), 0.6F, 1.8F)
        Particle.SPELL_INSTANT.spawn(player.location.add(0.0, 1.0, 0.0), 0.35F, 12)
    }

    companion object {
        private const val DAMAGE = 2.0
    }

}
