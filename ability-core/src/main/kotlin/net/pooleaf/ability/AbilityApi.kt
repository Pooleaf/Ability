package net.pooleaf.ability

import net.pooleaf.ability.ability.AbilityManager
import net.pooleaf.ability.ability.AbilityService
import net.pooleaf.ability.compat.CompatPluginManager
import net.pooleaf.ability.compat.CompatPluginService
import net.pooleaf.ability.configs.AbilityBlacklistConfig
import net.pooleaf.ability.configs.AbilityGameConfig
import net.pooleaf.ability.game.AbilityGame
import net.pooleaf.ability.player.AbilityPlayerManager
import net.pooleaf.ability.sidebar.AbilitySideBar
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.GameCore
import java.io.File

object AbilityApi {

    object unsafe {
        val abilityGameConfig: AbilityGameConfig by lazy {
            AbilityGameConfig(File(GameCore.gamePlugin.dataFolder, "ability-game-config.yml"))
        }

        val abilityBlacklistConfig: AbilityBlacklistConfig by lazy {
            AbilityBlacklistConfig(File(GameCore.gamePlugin.dataFolder, "ability-blacklist-config.yml"))
        }

        lateinit var playerManager: AbilityPlayerManager

        lateinit var abilityManager: AbilityManager
        lateinit var abilityService: AbilityService

        lateinit var compatPluginManager: CompatPluginManager
        lateinit var compatPluginService: CompatPluginService

        fun init() {
            playerManager = AbilityPlayerManager()

            abilityManager = AbilityManager()
            abilityService = AbilityService()

            compatPluginManager = CompatPluginManager()
            compatPluginService = CompatPluginService()
        }
    }


    val abilityGameConfig
        get() = unsafe.abilityGameConfig

    val abilityBlacklistConfig
        get() = unsafe.abilityBlacklistConfig

    val game: AbilityGame = AbilityGame()


    fun init() {
        unsafe.init()

        GameCore.unsafe.sideBarManager.sideBar = AbilitySideBar()

        loadConfig()
    }

    fun loadConfig() {
        abilityGameConfig.load()
        abilityGameConfig.save()

        abilityBlacklistConfig.load()
        abilityBlacklistConfig.save()
        // 설정을 다시 불러올 경우 능력을 제거해야함
        unsafe.abilityManager.removeBlacklistedAbilities().forEach {
            Logger.log("${it.fullName} 능력이 블랙리스트에 등록되었습니다.")
        }
    }

}