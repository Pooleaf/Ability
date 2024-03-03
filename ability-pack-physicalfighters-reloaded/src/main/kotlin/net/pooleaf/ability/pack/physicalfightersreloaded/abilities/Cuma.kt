package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.gamecore.utils.damageBypassAntiCheat
import net.pooleaf.gamecore.utils.toGamePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

/**
 * 공격 핵방지 우회
 */
class Cuma : Ability(), Listener {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "바솔로뮤 쿠마"
        rank = AbilityRank.SS
        type = AbilityType.PASSIVE
        description = listOf(
            "공격 받을 시 상대를 뒤로 넉백시키고, 일정 확률로 받은 공격을 적에게 되돌려줍니다.",
        )

        ban = false
    }

    @EventHandler
    fun onDamageByEntity(event: EntityDamageByEntityEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player != event.entity) return

        val player = event.entity as Player
        val damager = event.damager
        if (damager !is Player || damager.toGamePlayer()?.isPlaying() != true) return

        if (Math.random() <= 0.15) {
            damager.damageBypassAntiCheat(event.damage, player)
            event.isCancelled = true
        }

        damager.world.createExplosion(damager.location, 0.0F)
        damager.velocity = damager.velocity.add(player.location.toVector().subtract(damager.location.toVector()).normalize().multiply(-1))
    }

}