package net.pooleaf.gamecore.replay.data.block

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.Location
import org.bukkit.event.Listener

class BlockChangeData : RecordData, Listener {

    override val type: String = "blockChange"

    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var blockTypeId: Int = 0
    var blockData: Byte = 0


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer

        val location = Location(viewer.world, x.toDouble(), y.toDouble(), z.toDouble())
        viewer.sendBlockChange(location, blockTypeId, blockData)
    }

}

class BlockChangeDataListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.BLOCK_CHANGE) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val position = event.packet.blockPositionModifier.read(0)
        val packetBlockData = event.packet.blockData.read(0)

        val recordData = BlockChangeData().apply {
            x = position.x
            y = position.y
            z = position.z
            blockTypeId = packetBlockData.type.id
            blockData = packetBlockData.data.toByte()
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class BlockChangeDataReplayHandler : RecordDataReplayHandler<BlockChangeData> {

    override fun onPlay(replayPlayer: ReplayPlayer, recordData: BlockChangeData, tick: Long) {
        val viewer = replayPlayer.viewer

        val location = Location(viewer.world, recordData.x.toDouble(), recordData.y.toDouble(), recordData.z.toDouble())
        viewer.sendBlockChange(location, recordData.blockTypeId, recordData.blockData)
    }

    override fun onReversePlay(replayPlayer: ReplayPlayer, recordData: BlockChangeData, tick: Long) {
        TODO("Not yet implemented")
    }

    private fun getBeforeBlockData(replayPlayer: ReplayPlayer, location: Location, currentTick: Long) {
        // TODO
    }

}