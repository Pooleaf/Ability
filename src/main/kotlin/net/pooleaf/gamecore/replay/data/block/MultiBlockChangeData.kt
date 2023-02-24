package net.pooleaf.gamecore.replay.data.block

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.ChunkCoordIntPair
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo
import com.comphenix.protocol.wrappers.WrappedBlockData
import net.pooleaf.core.modules.support.bukkit.util.ItemUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.Location
import org.bukkit.event.Listener

class MultiBlockChangeData : RecordData, Listener {

    override val type: String = "multiBlockChange"

    var chunkX: Int = 0
    var chunkZ: Int = 0
    lateinit var blockChangeInfos: List<BlockChangeInfo>


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer

        val chunk = ChunkCoordIntPair(chunkX, chunkZ)
        val multiBlockChangeInfos = blockChangeInfos.map {
            val location = Location(viewer.location.world, it.x.toDouble(), it.y.toDouble(), it.z.toDouble())
            val wrappedBlockData = WrappedBlockData.createData(ItemUtil.getMaterial(it.blockTypeId), it.blockData)
            MultiBlockChangeInfo(location, wrappedBlockData)
        }.toTypedArray()

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE)
        packet.chunkCoordIntPairs.write(0, chunk)
        packet.multiBlockChangeInfoArrays.write(0, multiBlockChangeInfos)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}

class BlockChangeInfo() {
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var blockTypeId: Int = 0
    var blockData: Int = 0
}

class MultiBlockChangeDataListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.MULTI_BLOCK_CHANGE) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val packet = event.packet

        val chunk = packet.chunkCoordIntPairs.read(0)
        val multiBlockChangeInfos = packet.multiBlockChangeInfoArrays.read(0)
        val blockChangeInfos = multiBlockChangeInfos.map {
            BlockChangeInfo().apply {
                x = it.x
                y = it.y
                z = it.z
                blockTypeId = it.data.type.id
                blockData = it.data.data
            }
        }.toList()

        val recordData = MultiBlockChangeData().apply {
            chunkX = chunk.chunkX
            chunkZ = chunk.chunkZ
            this.blockChangeInfos = blockChangeInfos
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}