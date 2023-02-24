package net.pooleaf.gamecore.replay.data.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.event.Listener

class EntityDestoryData : RecordData, Listener {

    override val type: String = "entityDestroy"

    lateinit var entityIds: Array<Int>


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY)
        packet.integerArrays.write(0, entityIds.map { it + replayPlayer.entityIdOffset }.toIntArray())
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}

class EntityDestoryDataListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.ENTITY_DESTROY), Listener {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val packet = event.packet

        val packetEntityIds = packet.integerArrays.read(0)

        val recordData = EntityDestoryData().apply {
            entityIds = packetEntityIds.toTypedArray()
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}