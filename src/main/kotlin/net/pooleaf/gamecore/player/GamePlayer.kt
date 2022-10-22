package net.pooleaf.gamecore.player

import net.pooleaf.core.modules.gui.bukkit.title.Title
import net.pooleaf.core.modules.support.common.player.AbstractPlayer
import net.pooleaf.gamecore.DefaultTitleBuilder
import net.pooleaf.gamecore.team.Team
import org.bukkit.entity.Player
import java.util.*

open class GamePlayer(uuid: UUID, val team: Team<GamePlayer>) : AbstractPlayer<Player>(uuid) {

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

}