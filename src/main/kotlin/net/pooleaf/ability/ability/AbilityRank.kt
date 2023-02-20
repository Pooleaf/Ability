package net.pooleaf.ability.ability

import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.core.modules.support.common.CommonChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class AbilityRank(
    val color: CommonChatColor,
    val displayItem: ItemStack
) {

    HIDDEN(CommonChatColor.BLACK, ItemStack(Material.BEDROCK)),
    SS(CommonChatColor.DARK_PURPLE, ItemStack(Material.OBSIDIAN)),
    S(CommonChatColor.AQUA, ItemStack(Material.DIAMOND_BLOCK)),
    A(CommonChatColor.YELLOW, ItemStack(Material.GOLD_BLOCK)),
    B(CommonChatColor.GREEN, ItemBuilder("159:5").build()), // 연두색 점토
    C(CommonChatColor.GRAY, ItemBuilder("159:8").build()), // 밝은 회색 점토

}