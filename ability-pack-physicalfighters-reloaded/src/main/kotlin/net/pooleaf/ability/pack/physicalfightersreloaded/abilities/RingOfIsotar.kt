package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent

/**
 * poksi.class
 */
class RingOfIsotar : Ability(), Listener {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "이슈타르의 링"
        rank = AbilityRank.SS
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 바라보는 방향으로 4 데미지의 화살을 두발 발사합니다."
        )

        ban = false
    }


    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player != event.player || event.player.itemInHand == null || event.player.itemInHand.type != Material.IRON_INGOT) return

        val arrow1 = event.player.launchProjectile(Arrow::class.java)
        arrow1.velocity = arrow1.velocity.multiply(3)
        val arrow2 = event.player.launchProjectile(Arrow::class.java)
        arrow2.velocity = arrow2.velocity.multiply(2)
    }

    @EventHandler
    fun onArrowDamage(event: EntityDamageByEntityEvent) {
        if (event.damager !is Arrow || (event.damager as Arrow).shooter != abilityPlayer?.player) return
        event.damage = 4.0
    }

    @EventHandler
    fun onArrowHit(event: ProjectileHitEvent) {
        if (event.entity.shooter != abilityPlayer?.player) return
        event.entity.remove()
    }

}