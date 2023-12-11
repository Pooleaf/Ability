package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class ThunderBolt : Ability(), Listener, CastByItemHandler, Cooldownable {

    init {
        pluginName = AbilityPlugin.instance.name

        name = "썬더볼트"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 5칸 내의 적에게 6 데미지를 줍니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 5_000L)


    override fun onCastByItem(
        event: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        val location = event.player.location

        event.player.getNearbyEntities(5.0, 5.0, 5.0).forEach { entity ->
            if (entity !is LivingEntity) return@forEach

            location.world.strikeLightningEffect(entity.location)
            entity.damage(6.0)
        }

        return true
    }

}