package net.pooleaf.ability.sidebar.elements

import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.sidebar.GameSideBarPersonalNamedTextElement

class AbilityElement: GameSideBarPersonalNamedTextElement("§c능력") {

    override fun getPriority(): Int {
        return 10
    }

    override fun getValueText(gamePlayer: GamePlayer): String {
        return (gamePlayer as AbilityPlayer).ability?.let { it.name } ?: ""
    }

    override fun isShow(gamePlayer: GamePlayer): Boolean {
        return (gamePlayer as AbilityPlayer).ability != null && gamePlayer.isPlaying()
    }

    override fun isUseSpace(): Boolean {
        return true
    }

}