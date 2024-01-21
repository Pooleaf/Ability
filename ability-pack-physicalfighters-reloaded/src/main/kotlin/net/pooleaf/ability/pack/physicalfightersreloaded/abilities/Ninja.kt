package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class Ninja : Ability(), Listener, CastByItemHandler, Cooldownable {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "닌자"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 화살을 발사합니다.",
            "화살에 맞을 경우",
            "10% 확률로 폭발,",
            "30% 확률로 화염,",
            "60% 확률로 쿨타임이 초기화됩니다."
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 10_000L)


    override fun onCastByItem(
        event: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        val arrow = event.player.launchProjectile(Arrow::class.java, event.player.location.direction)
        arrow.velocity = arrow.velocity.multiply(5)

        val random = Math.random()
        arrow.customName = random.toString()
        if (random < 0.3) {
            arrow.fireTicks = 20
        }

        return true
    }

    @EventHandler
    fun onArrowHit(event: EntityDamageByEntityEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (event.damager !is Arrow || (event.damager as Arrow).shooter != player?.player) return

        val random = event.damager.customName?.toDoubleOrNull() ?: return

        if (0.3 <= random && random < 0.9) {
            cooldownTimer.cancel()
            player?.sendMessageSafely("§e쿨타임이 초기화되었습니다.")
        }
        if (0.9 <= random) {
            event.entity.world.createExplosion(event.entity.location, 4.0F)
        }
    }

}