package net.pooleaf.ability.ability

import net.pooleaf.core.modules.support.common.CommonChatColor
import org.bukkit.Material

enum class AbilityRank(
    val color: CommonChatColor,
    val block: Material
) {

    S(CommonChatColor.GREEN, Material.EMERALD_BLOCK),
    A(CommonChatColor.AQUA, Material.DIAMOND_BLOCK),
    B(CommonChatColor.YELLOW, Material.GOLD_BLOCK),
    C(CommonChatColor.WHITE, Material.IRON_BLOCK),
    D(CommonChatColor.GRAY, Material.COAL_BLOCK),
    F(CommonChatColor.RED, Material.DIRT);

}