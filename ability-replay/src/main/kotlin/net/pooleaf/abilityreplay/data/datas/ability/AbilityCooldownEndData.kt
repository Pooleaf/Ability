package net.pooleaf.abilityreplay.data.datas.ability

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 능력 쿨타임 종료 데이터
 */
data class AbilityCooldownEndData(
    var playerUuid: UUID? = null,
    var abilityName: String? = null
) : RecordData {

    override val type: String = "abilityCooldownEnd"

}