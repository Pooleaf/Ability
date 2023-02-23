package net.pooleaf.gamecore.replay.data

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Listener

class BlockChangeData : RecordData, Listener {

    override val type: String = "blockPlace"

    lateinit var worldName: String
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var blockTypeId: Int = 0
    var blockData: Byte = 0


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer

        val location = Location(Bukkit.getWorld(worldName), x.toDouble(), y.toDouble(), z.toDouble())
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
            worldName = event.player.world.name
            x = position.x
            y = position.y
            z = position.z
            blockTypeId = packetBlockData.type.id
            blockData = packetBlockData.data.toByte()
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}