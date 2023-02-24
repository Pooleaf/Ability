package net.pooleaf.gamecore.replay.data.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.cryptomorin.xseries.XSound
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import java.util.*
import kotlin.random.Random

class CollectData : RecordData, Listener {

    override val type: String = "collect"

    var collectedEntityId: Int = 0
    lateinit var collectorPlayerUuid: UUID


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer
        val citizensNpc = replayPlayer.npcs.get(collectorPlayerUuid)?.citizensNpc ?: return

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.COLLECT)
        packet.integers.write(0, collectedEntityId + replayPlayer.entityIdOffset)
        packet.integers.write(1, citizensNpc.entity.entityId)
        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)

        XSound.ENTITY_ITEM_PICKUP.play(viewer, 0.2F, ((Random.nextFloat() - Random.nextFloat()) * 0.7F + 1.0F) * 2.0F)
    }

}

class CollectDataListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.COLLECT), Listener {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

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