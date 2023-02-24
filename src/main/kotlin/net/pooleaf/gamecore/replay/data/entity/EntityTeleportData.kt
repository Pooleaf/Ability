package net.pooleaf.gamecore.replay.data.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.event.Listener

class EntityTeleportData : RecordData, Listener {

    override val type: String = "entityTeleport"

    var entityId: Int = 0
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var yaw: Byte = 0
    var pitch: Byte = 0


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_TELEPORT)
        packet.integers.write(0, entityId + replayPlayer.entityIdOffset)
        packet.integers.write(1, x)
        packet.integers.write(2, y)
        packet.integers.write(3, z)
        packet.bytes.write(0, yaw)
        packet.bytes.write(1, pitch)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}

class EntityTeleportDataListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.ENTITY_TELEPORT), Listener {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

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