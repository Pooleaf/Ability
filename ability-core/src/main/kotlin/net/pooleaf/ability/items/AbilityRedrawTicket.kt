package net.pooleaf.ability.items

import com.cryptomorin.xseries.XMaterial
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import org.bukkit.inventory.ItemStack

enum class AbilityRedrawTicket(
    val displayName: String,
    private val rankLimit: AbilityRank?,
    val color: String
) {
    ALL("능력 재추첨권", null, "§b"),
    SS("SS등급+ 능력 재추첨권", AbilityRank.SS, "§5"),
    S("S등급+ 능력 재추첨권", AbilityRank.S, "§b"),
    A("A등급+ 능력 재추첨권", AbilityRank.A, "§e");

    val item: ItemStack
        get() = ItemBuilder(XMaterial.ENCHANTED_BOOK.parseMaterial())
            .displayName("$color$displayName")
            .lore("§f우클릭 시 능력을 다시 추첨합니다.")
            .build()

    val abilityRanks: List<AbilityRank>
        get() = when (rankLimit) {
            null -> listOf(AbilityRank.SS, AbilityRank.S, AbilityRank.A, AbilityRank.B, AbilityRank.C)
            AbilityRank.SS -> listOf(AbilityRank.SS)
            AbilityRank.S -> listOf(AbilityRank.SS, AbilityRank.S)
            AbilityRank.A -> listOf(AbilityRank.SS, AbilityRank.S, AbilityRank.A)
            else -> emptyList()
        }

    companion object {
        fun getByItem(item: ItemStack): AbilityRedrawTicket? {
            return values().firstOrNull { item.isSimilar(it.item) }
        }
    }

}
