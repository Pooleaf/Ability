package net.pooleaf.ability

import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.plugin.BukkitCorePlugin

class AbilityPlugin: BukkitCorePlugin() {

    companion object {
        lateinit var instance: AbilityPlugin
    }


    override fun onStart() {
        instance = this

        prefix = "§c[ Ability ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        loadConfig()
    }

    override fun onEnd() {

    }

    override fun onConfigLoaded() {

    }

}