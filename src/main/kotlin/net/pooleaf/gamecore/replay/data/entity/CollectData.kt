package net.pooleaf.gamecore.replay.data.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.cryptomorin.xseries.XSound
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*
import kotlin.random.Random

data class CollectData(
    var collectedEntityId: Int = 0,
    var collectorPlayerUuid: UUID? = null
) : RecordData, Listener {

    override val type: String = "collect"

}

class CollectDataRecordListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.COLLECT) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val packet = event.packet

        val packetCollectedEntityId = packet.integers.read(0)
        val packetCollectorEntityId = packet.integers.read(1)
        val collectorPlayer = Bukkit.getOnlinePlayers().filter { it.entityId == packetCollectorEntityId }.firstOrNull()
        if (collectorPlayer == null) return

        val recordData = CollectData().apply {
            collectedEntityId = packetCollectedEntityId
            collectorPlayerUuid = collectorPlayer.uniqueId
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class CollectDataReplayHandler : RecordDataReplayHandler<CollectData> {

    override fun onPlay(recordData: CollectData, viewer: Player) {
        val replayPlayer = GameCore.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.collectorPlayerUuid)?.citizensNpc ?: return

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.COLLECT)
        packet.integers.write(0, recordData.collectedEntityId + ReplayPlayer.ENTITY_ID_OFFSET)
        packet.integers.write(1, citizensNpc.entity.entityId)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)

        XSound.ENTITY_ITEM_PICKUP.play(viewer, 0.2F, ((Random.nextFloat() - Random.nextFloat()) * 0.7F + 1.0F) * 2.0F)
    }

}