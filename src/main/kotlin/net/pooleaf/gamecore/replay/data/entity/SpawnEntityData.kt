package net.pooleaf.gamecore.replay.data.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.replay.ReplayExitEvent
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class SpawnEntityData : RecordData, Listener {

    override val type: String = "spawnEntity"

    var entityId: Int = 0
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var optionalSpeedX: Int = 0
    var optionalSpeedY: Int = 0
    var optionalSpeedZ: Int = 0
    var yaw: Int = 0
    var pitch: Int = 0
    var objectType: Int = 0
    var objectData: Int = 0


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY)
        packet.integers.write(0, entityId)
        packet.integers.write(1, x)
        packet.integers.write(2, y)
        packet.integers.write(3, z)
        packet.integers.write(4, optionalSpeedX)
        packet.integers.write(5, optionalSpeedY)
        packet.integers.write(6, optionalSpeedZ)
        packet.integers.write(7, yaw)
        packet.integers.write(8, pitch)
        packet.integers.write(9, objectType)
        packet.integers.write(10, objectData)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}

class SpawnEntityDataListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.SPAWN_ENTITY), Listener {

    val spawnedEntityIds = arrayListOf<Int>()


    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val packet = event.packet

        val packetEntityId = packet.integers.read(0)
        val packetX = packet.integers.read(1)
        val packetY = packet.integers.read(2)
        val packetZ = packet.integers.read(3)
        val packetOptionalSpeedX = packet.integers.read(4)
        val packetOptionalSpeedY = packet.integers.read(5)
        val packetOptionalSpeedZ = packet.integers.read(6)
        val packetYaw = packet.integers.read(7)
        val packetPitch = packet.integers.read(8)
        val packetType = packet.integers.read(9)
        val packetObjectData = packet.integers.read(10)

        if (spawnedEntityIds.contains(packetEntityId)) return
        spawnedEntityIds.add(packetEntityId)

        val recordData = SpawnEntityData().apply {
            entityId = packetEntityId
            x = packetX
            y = packetY
            z = packetZ
            optionalSpeedX = packetOptionalSpeedX
            optionalSpeedY = packetOptionalSpeedY
            optionalSpeedZ = packetOptionalSpeedZ
            yaw = packetYaw
            pitch = packetPitch
            objectType = packetType
            objectData = packetObjectData
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

    @EventHandler
    fun onReplayExit(event: ReplayExitEvent) {
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY)
        packet.integerArrays.write(0, spawnedEntityIds.toIntArray())
        ProtocolLibrary.getProtocolManager().sendServerPacket(event.replayPlayer.viewer, packet)
    }

}