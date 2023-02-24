package net.pooleaf.gamecore.replay.data.player

import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

/**
 * 채팅 데이터
 * 관리자에게만 전송
 */
class PlayerChatData : RecordData {

    override val type: String = "playerChat"

    lateinit var playerUuid: UUID
    lateinit var message: String


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer
        if (!viewer.isOp) return

        val chatCommonPlayer = CommonSenderModule.getPlayer(playerUuid)
        val chatPlayerName = chatCommonPlayer?.displayName ?: "??"
        viewer.sendMessage("§7[기록] §f${chatPlayerName}§f: ${message}")
    }

}

class PlayerChatDataListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val player = event.player
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val record = GameCore.unsafe.recordManager.record!!
        val recordData = PlayerChatData().apply {
            playerUuid = player.uniqueId
            message = event.message
        }
        record.addRecordData(recordData)
    }

}