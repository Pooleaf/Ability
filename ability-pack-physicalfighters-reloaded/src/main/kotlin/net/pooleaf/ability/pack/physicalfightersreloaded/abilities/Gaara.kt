package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class Gaara : Ability(), Listener, CastByItemHandler, Cooldownable {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "가아라"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 보고 있는 장소에 모래를 떨어뜨리고 4초 후 폭발시킵니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 40_000L)


    override fun onCastByItem(
        event: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        val targetBlock = event.player.getTargetBlock(null as Set<Material>?, 40)
        if (targetBlock == null) {
            event.player.sendWarning("거리가 너무 멉니다.")
            return false
        }

        val location = targetBlock.location

        val sandLocation = location.clone()
        for (y in 4..8) {
            sandLocation.y = location.y + y
            for (x in -3..3) {
                for (z in -3..3) {
                    sandLocation.x = location.x + x
                    sandLocation.z = location.z + z
                    if (sandLocation.block.type == Material.BEDROCK || sandLocation.block.type == Material.BARRIER) continue
                    sandLocation.block.type = Material.SAND
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(PhysicalFightersReloadedPlugin.instance, {
            val explosionLocation = location.add(0.0, 2.0, 0.0)
            explosionLocation.world.createExplosion(explosionLocation, 5f)
            explosionLocation.world.createExplosion(explosionLocation, 5f)
            explosionLocation.world.createExplosion(explosionLocation, 5f)
        }, 80L)

        return true
    }

}