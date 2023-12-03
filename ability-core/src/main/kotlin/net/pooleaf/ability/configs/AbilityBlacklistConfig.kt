package net.pooleaf.ability.configs

import net.pooleaf.core.modules.annoconfig.common.SimpleAnnoConfig
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import java.io.File

class AbilityBlacklistConfig(file: File?) : SimpleAnnoConfig(file) {

    @ConfigName("능력 블랙리스트")
    var blacklist = arrayListOf("플러그인이름:능력이름")

}