package net.pooleaf.gamecore.replay.data.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.event.Listener

class EntityVelocityData : RecordData, Listener {

    override val type: String = "entityVelocity"

    var entityId: Int = 0
    var velocityX: Int = 0
    var velocityY: Int = 0
    var velocityZ: Int = 0


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_VELOCITY)
        packet.integers.write(0, entityId + replayPlayer.entityIdOffset)
        packet.integers.write(1, velocityX)
        packet.integers.write(2, velocityY)
        packet.integers.write(3, velocityZ)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}

class EntityVelocityDataListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.ENTITY_VELOCITY), Listener {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val packet = event.packet

        val packetEntityId = packet.integers.read(0)
        val packetVelocityX = packet.integers.read(1)
        val packetVelocityY = packet.integers.read(2)
        val packetVelocityZ = packet.integers.read(3)

        val recordData = EntityVelocityData().apply {
            entityId = packetEntityId
            velocityX = packetVelocityX
            velocityY = packetVelocityY
            velocityZ = packetVelocityZ
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}