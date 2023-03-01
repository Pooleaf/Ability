package net.pooleaf.gamecore.replay.data.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.entity.Player

data class EntityTeleportData(
    var entityId: Int = 0,
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var yaw: Byte = 0,
    var pitch: Byte = 0
) : RecordData {

    override val type: String = "entityTeleport"

}

class EntityTeleportDataRecordListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.ENTITY_TELEPORT) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val packet = event.packet

        val packetEntityId = packet.integers.read(0)
        val packetX = packet.integers.read(1)
        val packetY = packet.integers.read(2)
        val packetZ = packet.integers.read(3)
        val packetYaw = packet.bytes.read(0)
        val packetPitch = packet.bytes.read(1)

        val recordData = EntityTeleportData().apply {
            entityId = packetEntityId
            x = packetX
            y = packetY
            z = packetZ
            yaw = packetYaw
            pitch = packetPitch
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class EntityTeleportDataReplayHandler : RecordDataReplayHandler<EntityTeleportData> {

    override fun onPlay(recordData: EntityTeleportData, viewer: Player) {
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT)
        packet.integers.write(0, recordData.entityId + ReplayPlayer.ENTITY_ID_OFFSET)
        packet.integers.write(1, recordData.x)
        packet.integers.write(2, recordData.y)
        packet.integers.write(3, recordData.z)
        packet.bytes.write(0, recordData.yaw)
        packet.bytes.write(1, recordData.pitch)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}