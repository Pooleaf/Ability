package net.pooleaf.ability.replay.data.ability

import net.pooleaf.ability.event.ability.AbilityDurationStartEvent
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
data class AbilityDurationStartData(
    var playerUuid: UUID? = null,
    var abilityName: String? = null,
    var durationMillis: Long = 0
) : RecordData {

    override val type: String = "abilityDurationStart"

}

class AbilityDurationStartDataRecordListener : Listener {

    @EventHandler
    fun onAbilityDurationStart(event: AbilityDurationStartEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = AbilityDurationStartData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
            durationMillis = event.durationMillis
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class AbilityDurationStartDataReplayHandler : RecordDataReplayHandler<AbilityDurationStartData> {

    override fun onPlay(recordData: AbilityDurationStartData, viewer: Player) {
        // TODO 홀로그램

        val playerName = CommonSenderModule.getPlayer(recordData.playerUuid)?.displayName ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님의 능력 지속시간이 시작되었습니다. (지속시간: §f${String.format("%.1f", recordData.durationMillis / 1000)}§e초)")
    }

}