package net.pooleaf.abilityreplay.data.datas.ability

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 능력 할당 해제 데이터
 */
data class AbilityResignData(
    var playerUuid: UUID? = null,
    var abilityName: String? = null
) : RecordData {

    override val type: String = "abilityResign"

}