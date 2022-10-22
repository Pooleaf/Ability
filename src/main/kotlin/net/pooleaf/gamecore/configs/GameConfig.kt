package net.pooleaf.gamecore.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import org.bukkit.Location
import java.io.File

class GameConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("게임 시작 플레이어 수")
    var startPlayerCount = 2

    @ConfigName("스폰 위치")
    var spawnLocation: Location? = null

}