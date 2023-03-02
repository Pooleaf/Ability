package net.pooleaf.ability.replay.data.ability

import net.pooleaf.ability.event.ability.AbilityCooldownEndEvent
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
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

class AbilityCooldownEndDataRecordListener : Listener {

    @EventHandler
    fun onAbilityCooldownEnd(event: AbilityCooldownEndEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val recordData = AbilityCooldownEndData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class AbilityCooldownEndDataReplayHandler : RecordDataReplayHandler<AbilityCooldownEndData> {

    override fun onPlay(recordData: AbilityCooldownEndData, viewer: Player) {
        // TODO 홀로그램

        val playerName = CommonSenderModule.getPlayer(recordData.playerUuid)?.displayName ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님의 능력 쿨타임이 종료되었습니다.")
    }

}