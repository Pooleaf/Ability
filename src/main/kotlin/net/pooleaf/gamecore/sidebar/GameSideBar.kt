package net.pooleaf.gamecore.sidebar

import net.pooleaf.core.modules.gui.bukkit.sidebar.SideBar
import org.bukkit.entity.Player

abstract class GameSideBar(title: String) {

    private val sideBar = SideBar(title)

    val elements = arrayListOf<GameSideBarElement>()

    val viewers = arrayListOf<Player>()


    fun update() {
        val texts = elements.filter { it.show }
            .sortedByDescending { it.priority }
            .flatMap { it.texts }

        sideBar.texts = texts
    }

    private fun sendTo(player: Player) {
        sideBar.updateScoreboard(player.scoreboard)
    }

    fun showTo(player: Player) {
        if (viewers.contains(player)) error("Player is already use sidebar")

        viewers.add(player)
        sendTo(player)
    }

}