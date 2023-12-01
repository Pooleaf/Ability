package net.pooleaf.abilityreward.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import java.io.File

class AbilityRewardConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("킬 게임머니")
    var killMoney: Double = 100.0

    @ConfigName("더블킬 게임머니")
    var doubleKillMoney: Double = 100.0

    @ConfigName("트리플킬 게임머니")
    var tripleKillMoney: Double = 150.0

    @ConfigName("쿼드라킬 게임머니")
    var quadraKillMoney: Double = 200.0

    @ConfigName("펜타킬 게임머니")
    var pentaKillMoney: Double = 300.0

    @ConfigName("어시스트 게임머니")
    var assistMoney: Double = 10.0

    @ConfigName("우승 게임머니")
    var winMoneyFormula: String = "startPlayerCount * 100 / teamPlayerCount"

}