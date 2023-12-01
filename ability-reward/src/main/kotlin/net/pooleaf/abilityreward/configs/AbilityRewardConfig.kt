package net.pooleaf.abilityreward.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import java.io.File

class AbilityRewardConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("킬 게임머니")
    var killMoney: Double = 100.0

    @ConfigName("우승 게임머니")
    var winMoneyFormula: String = "startPlayerCount * 100 / teamPlayerCount"

}