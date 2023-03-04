package net.pooleaf.gamecore.replay.data.player

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

/**
 * 플레이어 퇴장 데이터
 */
data class PlayerQuitData(
    var playerUuid: UUID? = null,
) : RecordData {

    override val type: String = "playerQuit"

}

class PlayerQuitDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val player = event.player
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val recordData = PlayerQuitData().apply {
            playerUuid = player.uniqueId
        }
        val hideData = PlayerHideData().apply {
            playerUuid = player.uniqueId
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
        GameCore.unsafe.recordManager.record!!.addRecordData(hideData)
    }

}

class PlayerQuitDataReplayHandler : RecordDataReplayHandler<PlayerQuitData> {

    override fun onPlay(recordData: PlayerQuitData, viewer: Player) {
        val quitCommonPlayer = CommonSenderModule.getPlayer(recordData.playerUuid)
        val quitPlayerName = quitCommonPlayer?.displayName ?: recordData.playerUuid
        viewer.sendMessage("§7[리플레이] §f${quitPlayerName} §e님이 퇴장했습니다.")
    }

}