package net.pooleaf.gamecore.vote.map

import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryIcon
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InevntoryGuiClickEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.pageable.LargePageableGui
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.gamecore.GameCore
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MapVoteGui(val mapVote: MapVote): LargePageableGui ("맵 투표") {

    init {
        // 랜덤 투표 아이콘
        val randomIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                val randomVotedCount = GameCore.playerManager.getOnlineJoinedPlayers().size - mapVote.getVoteCount()

                return ItemBuilder(Material.EMPTY_MAP)
                    .amount(randomVotedCount)
                    .displayName("§e§l랜덤 (§f${randomVotedCount}§e명)")
                    .lore("§e클릭 시 §f랜덤§e에 투표합니다.")
                    .build()
            }

            override fun onClick(event: InevntoryGuiClickEvent?) {
                super.onClick(event)
            }
        }
    }

}