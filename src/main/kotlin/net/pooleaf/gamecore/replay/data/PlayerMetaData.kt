package net.pooleaf.gamecore.replay.data

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import java.util.*

class PlayerMetaData : RecordData {

    override val type: String = "playerMetadata"

    lateinit var playerUuid: UUID
    var index: Int = 0
    var value: Byte = 0


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val citizensNpc = replayPlayer.npcs.get(playerUuid)?.citizensNpc ?: return

        // 불 처리
        when (value % 2) {
            0 -> citizensNpc.entity.fireTicks = 0
            1 -> citizensNpc.entity.fireTicks = 9999999
        }

        println("metadata: ${index} / ${value}")

        val entityPlayer = BukkitReflectionUtil.getHandle(citizensNpc.entity) as EntityPlayer
        val dataWatcher = entityPlayer.dataWatcher
        dataWatcher.watch(index, value)
        val packet = PacketPlayOutEntityMetadata(citizensNpc.entity.entityId, dataWatcher, false)
        BukkitReflectionUtil.sendPacket(replayPlayer.viewer, packet)
    }

}

class PlayerMetaDataListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.ENTITY_METADATA) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val packet = event.packet
        val entityId = packet.integers.read(0)
        val entity = packet.getEntityModifier(event.player.world).read(0)

        // 본인 것만 녹화
        if (entityId != entity.entityId || entityId != event.player.entityId) return

        val entityMetaData = packet.watchableCollectionModifier.read(0)
        if (entityMetaData.isEmpty()) return

        val recordData = PlayerMetaData().apply {
            playerUuid = event.player.uniqueId
            index = entityMetaData.get(0).index
            value = entityMetaData.get(0).value as Byte
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}