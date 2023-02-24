package net.pooleaf.gamecore.replay.data.block

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.WrappedChatComponent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent

/**
 * 블럭 설치 사운드 재생용
 * 블럭 변경은 [BlockChangeData]에서 담당
 */
class UpdateSignData : RecordData {

    override val type: String = "updateSign"

    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    lateinit var lines: Array<String>


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer
        if (!viewer.isOp) return

        val wrappedChatComponents = lines.map { WrappedChatComponent.fromLegacyText(it) }.toTypedArray()

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.UPDATE_SIGN)
        packet.blockPositionModifier.write(0, BlockPosition(x, y, z))
        packet.chatComponentArrays.write(0, wrappedChatComponents)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}

class UpdateSignDataListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onSignChange(event: SignChangeEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val block = event.block
        val location = block.location

        val record = GameCore.unsafe.recordManager.record!!
        val recordData = UpdateSignData().apply {
            x = location.x.toInt()
            y = location.y.toInt()
            z = location.z.toInt()
            lines = event.lines
        }
        record.addRecordData(recordData)
    }

}