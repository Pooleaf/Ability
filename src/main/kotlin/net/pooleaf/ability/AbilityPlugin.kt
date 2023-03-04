package net.pooleaf.ability

import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.plugin.BukkitCorePlugin
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.player.GamePlayerManager

class AbilityPlugin: BukkitCorePlugin() {

    companion object {
        lateinit var instance: AbilityPlugin
    }

    override fun onStart() {
        instance = this

        prefix = "§c[ Ability ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        GameCore.init(this, AbilityApi.game)
        AbilityApi.init()
        GameCore.unsafe.playerManager = AbilityApi.unsafe.playerManager as GamePlayerManager<GamePlayer>

        AbilityApi.unsafe.abilityManager.registerAbilities(this)
        AbilityApi.unsafe.compatPluginService.enableAllCompatPlugins()

        registerEventListeners()
        registerCommonEventListeners()
        registerCommands()

        loadConfig()
    }

    override fun onEnd() {

    }

    override fun onConfigLoaded() {
        GameCore.loadConfig()
        AbilityApi.loadConfig()
    }

}