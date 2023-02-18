package net.pooleaf.gamecore.startitem

import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryGui
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryIcon
import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryPanel
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InevntoryGuiClickEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiCloseEvent
import net.pooleaf.core.modules.gui.bukkit.sign.SignGui
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.gamecore.GameCore
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class StartItemEditGui: InventoryGui("시작 아이템 수정", 6) {

    val armorPanel: InventoryPanel
    val itemPanel: InventoryPanel

    init {
        armorPanel = createPanel("armorPanel", 1, 1, 9, 2)
        itemPanel = createPanel("itemPanel", 1, 3, 9, 3)

        // 구분선
        val decoIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                return ItemBuilder(Material.STAINED_GLASS_PANE)
                    .displayName("§f")
                    .build()
            }
        }

        for (x in 5..8) {
            armorPanel.set(x, 1, decoIcon)
        }

        for (x in 1..9) {
            armorPanel.set(x, 2, decoIcon)
        }

        // 시작 아이템 넣기
        val startItem = GameCore.unsafe.startItemManager.startItem
        startItem.items.forEach { itemPanel.add(it) }

        // 레벨
        val levelIcon = object : InventoryIcon() {
            override fun updateItem(): ItemStack {
                return ItemBuilder(Material.EXP_BOTTLE)
                    .displayName("${startItem.level} §e§l레벨")
                    .lore("§f클릭 시 레벨을 수정합니다.")
                    .build()
            }

            override fun onClick(event: InevntoryGuiClickEvent) {
                // 표지판으로 입력
                object : SignGui("레벨을 입력해 주세요") {
                    override fun onSignComplete(player: Player, lines: Array<out String>) {
                        val level = lines[2].toIntOrNull()
                        if (level == null) {
                            player.sendWarning("레벨은 숫자만 입력 가능합니다.")
                            return
                        }

                        GameCore.unsafe.startItemManager.startItem.level = level
                        player.sendMessage("§b시작 레벨을 §f${level}§b(으)로 설정했습니다.")

                        this@StartItemEditGui.updateAsynchronously()
                    }
                }.open(event.player)
            }
        }
        armorPanel.set(9, 1, levelIcon)
    }

    override fun onClick(event: InevntoryGuiClickEvent) {
        event.isCancelled = false

        // 갑옷 & 레벨
        if (event.clickedPanel == armorPanel) {
            when (event.x) {
                in 1..4 -> event.isCancelled = false
                9 -> event.isCancelled = false
            }
        }

        // 아이템 패널
        if (event.clickedPanel == itemPanel) {
            event.isCancelled = false
        }
    }

    override fun onClose(event: InventoryGuiCloseEvent) {
        val startItem = GameCore.unsafe.startItemManager.startItem

        // 갑옷
        startItem.helmetItem = armorPanel.get(1, 1) as ItemStack
        startItem.chestplatItem = armorPanel.get(2, 1) as ItemStack
        startItem.leggingsItem = armorPanel.get(3, 1) as ItemStack
        startItem.bootsItem = armorPanel.get(4, 1) as ItemStack

        // 아이템
        startItem.items.clear()
        itemPanel.items.values.forEach { startItem.items.add(it as ItemStack) }

        startItem.saveStartItemConfig()
        event.player.sendMessage("§b시작 아이템을 저장했습니다.")
    }

}