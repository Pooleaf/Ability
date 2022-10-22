package net.pooleaf.gamecore.player

import net.pooleaf.core.modules.gui.GuiModule
import net.pooleaf.core.modules.gui.bukkit.title.Title
import net.pooleaf.core.modules.support.common.player.AbstractPlayer
import net.pooleaf.gamecore.DefaultTitleBuilder
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.quickbars.ObserverQuickBar
import net.pooleaf.gamecore.team.Team
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*

open class GamePlayer(uuid: UUID, val team: Team) : AbstractPlayer<Player>(uuid) {

    var joined = false // 게임 참여 여부
    var defeated = false // 패배 여부
    var observer = false // 관전 모드


    fun sendTitle(title: Title) {
        title.send(player)
    }

    fun sendTitle(title: String?) {
        sendTitle(
            DefaultTitleBuilder()
                .title(title)
                .build()
        )
    }

    fun sendTitle(title: String?, subtitle: String?) {
        sendTitle(
            DefaultTitleBuilder()
                .title(title)
                .subtitle(subtitle)
                .build()
        )
    }

    fun toggleObserver(toggle: Boolean) {
        player.health = player.maxHealth
        player.level = 0
        player.exp = 0F
        player.inventory!!.clear()
        player.updateInventory()

        if (toggle) {
            player.gameMode = GameMode.ADVENTURE
            player.allowFlight = true
            player.isFlying = true

            ObserverQuickBar().setTo(player)

            observer = true
        } else {
            if (GameCore.game.gameStarted) {
                player.gameMode = GameMode.SURVIVAL
            } else {
                player.gameMode = GameMode.ADVENTURE
            }

            player.allowFlight = false
            player.isFlying = false

            GuiModule.getQuickBarManager().removeTo(player)

            observer = false
        }
    }

}