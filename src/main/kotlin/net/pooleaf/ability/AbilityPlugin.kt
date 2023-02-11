package net.pooleaf.ability

import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.plugin.BukkitCorePlugin
import net.pooleaf.gamecore.v1.GameCore
import net.pooleaf.gamecore.v1.player.GamePlayer
import net.pooleaf.gamecore.v1.player.GamePlayerManager

class AbilityPlugin: BukkitCorePlugin() {

    companion object {
        lateinit var instance: AbilityPlugin
    }


    override fun onStart() {
        instance = this

        prefix = "§c[ Ability ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        GameCore.init(this, AbilityApi.game, AbilityApi.playerManager as GamePlayerManager<GamePlayer>)

        registerEventListeners()
        registerCommonEventListeners()
        registerCommands()

        AbilityApi.abilityManager.registerAbilities(this)

        loadConfig()
    }

    override fun onEnd() {

    }

    override fun onConfigLoaded() {
        GameCore.loadConfig()
    }

}