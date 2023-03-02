package net.pooleaf.ability.replay.data.ability

import net.pooleaf.ability.event.ability.AbilityResignEvent
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
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

class AbilityResignDataRecordListener : Listener {

    @EventHandler
    fun onAbilityResign(event: AbilityResignEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val recordData = AbilityResignData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class AbilityResignDataReplayHandler : RecordDataReplayHandler<AbilityResignData> {

    override fun onPlay(recordData: AbilityResignData, viewer: Player) {
        // TODO 홀로그램

        val playerName = CommonSenderModule.getPlayer(recordData.playerUuid)?.displayName ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님의 능력이 삭제되었습니다.")
    }

}