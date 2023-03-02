package net.pooleaf.ability.replay.data.ability

import net.pooleaf.ability.event.ability.AbilityAssignEvent
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
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

class AbilityAssignDataRecordListener : Listener {

    @EventHandler
    fun onAbilityAssign(event: AbilityAssignEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val recordData = AbilityAssignData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class AbilityAssignDataReplayHandler : RecordDataReplayHandler<AbilityAssignData> {

    override fun onPlay(recordData: AbilityAssignData, viewer: Player) {
        // TODO 홀로그램

        val playerName = CommonSenderModule.getPlayer(recordData.playerUuid)?.displayName ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님의 능력: §f${recordData.abilityName}")
    }

}