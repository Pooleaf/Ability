package net.pooleaf.ability.replay.data.ability

import net.pooleaf.ability.event.ability.AbilityDurationEndEvent
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

/**
 * 능력 지속 시간 시작 데이터
 */
data class AbilityDurationEndData(
    var playerUuid: UUID? = null,
    var abilityName: String? = null
) : RecordData {

    override val type: String = "abilityDurationStart"

}

class AbilityDurationEndDataRecordListener : Listener {

    @EventHandler
    fun onAbilityDurationEnd(event: AbilityDurationEndEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = AbilityDurationEndData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class AbilityDurationEndDataReplayHandler : RecordDataReplayHandler<AbilityDurationEndData> {

    override fun onPlay(recordData: AbilityDurationEndData, viewer: Player) {
        // TODO 홀로그램

        val playerName = CommonSenderModule.getPlayer(recordData.playerUuid)?.displayName ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님의 능력 지속시간이 종료되었습니다.")
    }

}