package net.pooleaf.gamecore.quickbar

import net.pooleaf.core.modules.gui.bukkit.inventory.InventoryIcon
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InevntoryGuiClickEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.events.InventoryGuiCloseEvent
import net.pooleaf.core.modules.gui.bukkit.inventory.pageable.LargePageableGui
import net.pooleaf.core.modules.support.bukkit.messager.sendWarningSafely
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.GameCore
import org.bukkit.inventory.ItemStack

class SpectatorTeleporterGui: LargePageableGui("관전할 플레이어를 선택하세요.") {

    init {
        updatePlayers()
    }

    fun updatePlayers() {
        clear()

        GameCore.unsafe.playerManager.getOnlinePlayingPlayers().forEach { gamePlayer ->
            println("11111111111${gamePlayer.name}")
            addItem(object : InventoryIcon() {
                val gamePlayer1 = gamePlayer

                override fun updateItem(): ItemStack {
                    return ItemBuilder()
                        .skull(this.gamePlayer1.name)
                        .displayName("§e§l${this.gamePlayer1.displayName}")
                        .lore("§f클릭 시 §e순간이동§f합니다.")
                        .build()
                }

                override fun onClick(event: InevntoryGuiClickEvent) {
                    val player = event.player

                    if (!this.gamePlayer1.isPlaying()) {
                        player.sendWarningSafely("이미 탈락한 플레이어입니다.")
                        return
                    }

                    if (!this.gamePlayer1.isOnline) {
                        player.sendWarningSafely("접속 중이 아닌 플레이어입니다.")
                        return
                    }

                    TeleportUtil.teleport(player, this.gamePlayer1.player.location)
                    player.sendMessage("${this.gamePlayer1.displayName} §e님께 텔레포트했습니다.")
                }
            })
        }

        if (currentPage > maxPage) {
            currentPage = maxPage
        }
        gotoPage(currentPage)
        updateAsynchronously()
    }

    override fun onClose(event: InventoryGuiCloseEvent) {
        GameCore.unsafe.quickBarManager.spectatorQuickBar.spectatorTeleporterGuis.remove(event.player.uniqueId)
    }

}