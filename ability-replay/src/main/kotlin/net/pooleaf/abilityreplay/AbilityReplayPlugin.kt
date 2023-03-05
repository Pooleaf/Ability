package net.pooleaf.abilityreplay

import net.pooleaf.core.modules.commonevent.CommonEventModule
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.plugin.BukkitCorePlugin
import org.bukkit.Bukkit

class AbilityReplayPlugin: BukkitCorePlugin() {

    companion object {
        lateinit var instance: AbilityReplayPlugin
    }


    override fun onStart() {
        instance = this

        prefix = "§c[ AbilityReplay ]"
        color = CommonChatColor.RED
        registerLoggerPrefix()

        init()
        if (Bukkit.getPluginManager().getPlugin("GameCore") != null) {
            registerEventListeners()
        }
    }

    fun init() {
        AbilityReplayApi.init()

        Logger.log("플러그인이 초기화되었습니다.")
    }

}