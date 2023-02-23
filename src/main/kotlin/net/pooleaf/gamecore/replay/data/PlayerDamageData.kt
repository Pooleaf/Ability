package net.pooleaf.gamecore.replay.data

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.cryptomorin.xseries.XSound
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.*

class PlayerDamageData : RecordData {

    override val type: String = "playerDamage"

    lateinit var playerUuid: UUID


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val citizensNpc = replayPlayer.npcs.get(playerUuid)?.citizensNpc ?: return

//        PacketPlayOutEntityStatus
//        PacketPlayOutEntityMetadata
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ANIMATION)
        packet.integers.write(0, citizensNpc.entity.entityId)
        packet.integers.write(1, 1)
        ProtocolLibrary.getProtocolManager().sendServerPacket(replayPlayer.viewer, packet)

        XSound.ENTITY_PLAYER_HURT.play(replayPlayer.viewer, 0.8F, 1.0F)
    }

}

class PlayerDamageDataListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDamage(event: PlayerDamageEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val player = event.player
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val record = GameCore.unsafe.recordManager.record!!
        val damageRecordData = PlayerDamageData().apply {
            playerUuid = player.uniqueId
        }
        record.addRecordData(damageRecordData)
    }

}