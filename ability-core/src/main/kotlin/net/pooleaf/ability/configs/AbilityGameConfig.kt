package net.pooleaf.ability.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import java.io.File

class AbilityGameConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("능력 중복 허용")
    var allowAbilityDuplicate = false

    @ConfigName("능력 강제 확정 시간(초)")
    var drawSkipSeconds: Int = 90

    @ConfigName("무적 시간(초)")
    var godModeSeconds: Int = 60 * 3

    @ConfigName("경계선 랜덤")
    var useRandomWorldBorderCenter: Boolean = true

    @ConfigName("경계선 축소.첫번째.시간")
    var firstWorldBorderReduceWaitSeconds: Int = 60 * 5

    @ConfigName("경계선 축소.첫번째.초당 축소 크기")
    var firstWorldBorderReduceSizePerSeconds: Int = 5

    @ConfigName("경계선 축소.두번째.시간")
    var secondWorldBorderReduceWaitSeconds: Int = 60 * 3

    @ConfigName("경계선 축소.두번째.초당 축소 크기")
    var secondWorldBorderReduceSizePerSeconds: Int = 5

    @ConfigName("경계선 축소.세번째.시간")
    var thirdWorldBorderReduceWaitSeconds: Int = 60 * 3

    @ConfigName("경계선 축소.세번째.초당 축소 크기")
    var thirdWorldBorderReduceSizePerSeconds: Int = 5

    @ConfigName("보급품.사용")
    var useSupply: Boolean = true

    @ConfigName("보급품.생성 간격")
    var supplyCreateIntervalSeconds: Int = 5 * 60

}
