package net.pooleaf.ability.pack.pooleaf.abilities

import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.Durationable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class Leap : Ability(), Listener, CastByItemHandler, Cooldownable, Durationable {

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "도약"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 우클릭 시 앞으로 도약합니다.",
            "도약 중에는 낙사 데미지를 받지 않습니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 60_000L)
    override val durationTimer: DurationTimer = DurationTimer(this, 5_000L)


    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        if (clickType != CastByItemHandler.ClickType.RIGHT) return false

        val player = abilityPlayer?.player ?: return false

        val direction = player.location.direction.normalize()
        player.velocity = direction.multiply(LEAP_POWER).setY(LEAP_HEIGHT)

        player.world.playSound(player.location, XSound.ENTITY_ENDER_DRAGON_FLAP.parseSound(), 0.8F, 1.2F)
        Particle.CLOUD.spawn(player.location, 0.2F, 16)

        return true
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player != event.entity) return
        if (!durationTimer.isRunning) return
        if (event.cause != EntityDamageEvent.DamageCause.FALL) return

        event.isCancelled = true
    }

    companion object {
        private const val LEAP_POWER = 1.6
        private const val LEAP_HEIGHT = 0.7
    }

}
