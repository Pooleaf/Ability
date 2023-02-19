package net.pooleaf.ability.compat

import net.pooleaf.ability.AbilityApi
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.core.modules.support.common.logger.Logger
import org.bukkit.plugin.java.JavaPlugin

class CompatPluginService {

    /**
     * 호환 플러그인을 활성화 시킵니다.
     */
    fun enableCompatPlugin(compatPlugin: CompatPlugin<out JavaPlugin, *>): Boolean {
        if (!compatPlugin.existsPlugin()) error("${compatPlugin.name} plugin not exists")

        try {
            Logger.log("${compatPlugin.name} 호환 플러그인을 초기화하는 중입니다..")

            // 활성화
            compatPlugin.onEnable()
            compatPlugin.isEnable = true

            // 능력 불러오기
            val compatAbilities = compatPlugin.loadAbilities()
            AbilityApi.unsafe.abilityManager.registerAbilities(compatAbilities)
            Logger.log("${compatAbilities.size}개의 능력을 불러왔습니다.")

            // 플러그인의 Listener, 명령어 등록 해제
            val plugin = compatPlugin.getPlugin()
            BukkitReflectionUtil.unregisterListeners(plugin)
            BukkitReflectionUtil.unregisterCommands(plugin)

            Logger.log("${compatPlugin.name} 호환 플러그인이 활성화되었습니다.")
            return true
        } catch (exception: Exception) {
            exception.printStackTrace()

            compatPlugin.isEnable = false

            Logger.warning("${compatPlugin.name} 호환 플러그인을 불러올 수 없습니다.")
            return false
        }
    }

    /**
     * 모든 호환 플러그인을 활성화시킵니다.
     */
    fun enableAllCompatPlugins() {
        AbilityApi.unsafe.compatPluginManager.values().forEach { enableCompatPlugin(it) }
    }

}