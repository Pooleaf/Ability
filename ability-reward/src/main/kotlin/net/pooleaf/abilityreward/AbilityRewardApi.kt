package net.pooleaf.abilityreward

import net.pooleaf.abilityreward.configs.AbilityRewardConfig
import net.pooleaf.abilityreward.services.AbilityRewardService
import net.pooleaf.abilityreward.services.JavaScriptService
import net.pooleaf.gamecore.GameCore
import java.io.File

object AbilityRewardApi {

    object unsafe {
        val abilityRewardConfig: AbilityRewardConfig by lazy {
            AbilityRewardConfig(File(GameCore.gamePlugin.dataFolder, "ability-reward-config.yml"))
        }

        val javaScriptService: JavaScriptService = JavaScriptService()
        val abilityRewardService: AbilityRewardService = AbilityRewardService()

        fun init() {
            reloadConfig()
        }

        fun reloadConfig() {
            abilityRewardConfig.load()
            javaScriptService.clearScriptEngine()

            // 우승 게임머니 계산 함수 설정
            abilityRewardService.createCalculateWinMoneyFunction(abilityRewardConfig.winMoneyFormula)
        }
    }


    fun init() {
        unsafe.init()
    }

}