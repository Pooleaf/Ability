package net.pooleaf.abilityreplay.data.datas.ability

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 능력 할당 데이터
 */
data class AbilityAssignData(
    var playerUuid: UUID? = null,
    var abilityName: String? = null
) : RecordData {

    override val type: String = "abilityAssign"

}