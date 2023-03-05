package net.pooleaf.abilityreplay.data.datas.ability

import net.pooleaf.gamereplay.data.RecordData
import java.util.*

/**
 * 능력 쿨타임 시작 데이터
 */
data class AbilityCooldownStartData(
    var playerUuid: UUID? = null,
    var abilityName: String? = null,
    var cooldownMillis: Long = 0
) : RecordData {

    override val type: String = "abilityCooldownStart"

}