package net.pooleaf.gamecore.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import java.io.File

class AutoGameConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("게임 시작 팀 수")
    var startTeamCount = 2

    @ConfigName("재접속 허용 시간(초)")
    var reconnectAllowTime = 180

    @ConfigName("우승 허용 시간(초)")
    var winAllowTime = 30

}