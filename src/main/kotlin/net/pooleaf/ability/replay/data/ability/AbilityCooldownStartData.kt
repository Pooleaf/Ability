package net.pooleaf.ability.replay.data.ability

import net.pooleaf.ability.event.ability.AbilityCooldownStartEvent
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
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

class AbilityCooldownStartDataRecordListener : Listener {

    @EventHandler
    fun onAbilityCooldownStart(event: AbilityCooldownStartEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val recordData = AbilityCooldownStartData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
            cooldownMillis = event.cooldownMillis
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class AbilityCooldownStartDataReplayHandler : RecordDataReplayHandler<AbilityCooldownStartData> {

    override fun onPlay(recordData: AbilityCooldownStartData, viewer: Player) {
        // TODO 홀로그램

        val playerName = CommonSenderModule.getPlayer(recordData.playerUuid)?.displayName ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님이 능력을 사용했습니다. (쿨타임: §f${String.format("%.1f", recordData.cooldownMillis / 1000)}§e초)")
    }

}