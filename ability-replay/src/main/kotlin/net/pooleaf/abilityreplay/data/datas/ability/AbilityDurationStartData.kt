package net.pooleaf.abilityreplay.data.datas.ability

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 능력 지속 시간 시작 데이터
 */
data class AbilityDurationStartData(
    var playerUuid: UUID? = null,
    var abilityName: String? = null,
    var durationMillis: Long = 0
) : RecordData {

    override val type: String = "abilityDurationStart"

}