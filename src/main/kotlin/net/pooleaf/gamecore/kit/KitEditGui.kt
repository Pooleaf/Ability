package net.pooleaf.gamecore.kit

import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryGui
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InevntoryGuiClickEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiCloseEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class KitEditGui(
    val kit: Kit,
    val player: Player
): InventoryGui(kit.name, 3) {

    init {
        clickDelayMillis = 0
        kit.items.forEach { index, item -> mainPanel.set(index, item) }
    }

    override fun onClick(event: InevntoryGuiClickEvent) {
        event.isCancelled = false
    }

    override fun onClose(event: InventoryGuiCloseEvent) {
        mainPanel.items.forEach { index, item -> kit.items.put(index, item as ItemStack) }
        kit.saveKitConfig()

        player.sendMessage("${kit.name} §b킷을 저장했습니다.")
    }

}