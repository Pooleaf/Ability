package net.pooleaf.abilityreward

import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.plugin.BukkitCorePlugin

class AbilityRewardPlugin: BukkitCorePlugin() {

    companion object {
        lateinit var instance: AbilityRewardPlugin
    }


    override fun onStart() {
        instance = this

        prefix = "§c[ AbilityReward ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        AbilityRewardApi.init()
        registerEventListeners()
    }

    override fun onConfigLoaded() {
        AbilityRewardApi.unsafe.reloadConfig()
    }

}