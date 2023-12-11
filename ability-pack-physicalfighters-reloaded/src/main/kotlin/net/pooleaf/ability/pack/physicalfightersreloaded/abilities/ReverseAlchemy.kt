package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.core.modules.support.bukkit.util.InventoryUtil
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class ReverseAlchemy : Ability(), CastByItemHandler, Cooldownable {

    init {
        pluginName = AbilityPlugin.instance.name

        name = "반 연금술"
        rank = AbilityRank.S
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 좌클릭 시 금괴 1개를 철괴 1개로 교환합니다.",
            "철괴 우클릭 시 금괴 3개를 다이아몬드 1개로 교환합니다.",
            "금괴 클릭 시 금괴 3개를 소모하여 체력과 허기를 모두 회복합니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 30_000L)


    override fun onCastByItem(
        event: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        val player = event.player
        val goldAmount: Int = InventoryUtil.getItemAmount(player.inventory, ItemStack(Material.GOLD_INGOT))

        when (item.type) {
            Material.IRON_INGOT -> {
                if (clickType === CastByItemHandler.ClickType.LEFT) {
                    if (goldAmount < 1) {
                        player.sendWarning("금괴가 부족합니다.")
                        return true
                    }

                    InventoryUtil.takeItem(player.inventory, ItemStack(Material.GOLD_INGOT), 1)
                    player.inventory.addItem(ItemStack(Material.IRON_INGOT)).forEach { (index, item) -> player.world.dropItemNaturally(player.location, item) }

                    player.sendMessage("§e금괴 1개를 철괴 1개로 교환했습니다.")
                } else {
                    if (goldAmount < 3) {
                        player.sendWarning("금괴가 부족합니다.")
                        return true
                    }

                    InventoryUtil.takeItem(player.inventory, ItemStack(Material.GOLD_INGOT), 3)
                    player.inventory.addItem(ItemStack(Material.DIAMOND)).forEach { (index, item) -> player.world.dropItemNaturally(player.location, item) }

                    player.sendMessage("금괴 3개를 다이아몬드 1개로 교환했습니다.")
                }
            }

            Material.GOLD_INGOT -> {
                if (goldAmount < 3) {
                    player.sendWarning("금괴가 부족합니다.")
                    return true
                }

                InventoryUtil.takeItem(player.inventory, ItemStack(Material.GOLD_INGOT), 3)
                player.foodLevel = 20
                player.saturation = 5.0F
                player.health = player.maxHealth

                player.sendMessage("§e금괴 3개를 소모하여 체력과 허기를 모두 회복했습니다.")
            }

            else -> return false
        }

        return true
    }

}