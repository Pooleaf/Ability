package net.pooleaf.gamecore.replay.data.player

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

class PlayerMoveData : RecordData {

    override val type: String = "playerMove"

    lateinit var playerUuid: UUID
    lateinit var worldName: String
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0
    var yaw: Float = 0.0F
    var pitch: Float = 0.0F


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val citizensNpc = replayPlayer.npcs.get(playerUuid)?.citizensNpc ?: return

        val location = Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch)
        citizensNpc.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)
    }

}

class PlayerMoveDataListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val player = event.player
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val location = event.to

        val record = GameCore.unsafe.recordManager.record!!
        val recordData = PlayerMoveData().apply {
            playerUuid = player.uniqueId
            worldName = location.world.name
            x = location.x
            y = location.y
            z = location.z
            yaw = location.yaw
            pitch = location.pitch
        }
        record.addRecordData(recordData)
    }

}