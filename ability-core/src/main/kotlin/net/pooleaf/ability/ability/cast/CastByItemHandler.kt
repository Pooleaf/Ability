package net.pooleaf.ability.ability.cast

import net.pooleaf.ability.ability.Cooldownable
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

interface CastByItemHandler: Cooldownable {

    /**
     * 능력 캐스팅에 필요한 아이템
     */
    val castItem: List<ItemStack>

    fun isCastItem(item: ItemStack) = castItem.any { item.isSimilar(it) }

    /**
     * [castItem] 아이템으로 능력을 캐스팅할 경우 호출됩니다.
     * 캐스팅 성공 여부를 반환하며, 캐스팅 성공 시 [cooldownTimer]가 시작됩니다.
     */
    fun onCastByItem(playerInteractEvent: PlayerInteractEvent, item: ItemStack, clickType: ClickType): Boolean


    /**
     * 능력 캐스팅 시 클릭 타입
     */
    enum class ClickType {
        LEFT,
        RIGHT
    }

}