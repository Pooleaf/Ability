package net.pooleaf.gamecore.replay.data.player

import net.citizensnpcs.util.PlayerAnimation
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerAnimationType
import java.util.*

data class PlayerAnimationData(
    var playerUuid: UUID? = null,
    var animationType: String? = null
) : RecordData {

    override val type: String = "playerAnimation"

}

class PlayerAnimationDataRecordListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerAnimation(event: PlayerAnimationEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val player = event.player
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val recordData = PlayerAnimationData().apply {
            playerUuid = player.uniqueId
            animationType = event.animationType.name
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class PlayerAnimationDataReplayHandler : RecordDataReplayHandler<PlayerAnimationData> {

    override fun onPlay(recordData: PlayerAnimationData, viewer: Player) {
        val replayPlayer = GameCore.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid)?.citizensNpc ?: return

        val animationType = PlayerAnimationType.valueOf(recordData.animationType!!)
        if (animationType == PlayerAnimationType.ARM_SWING) {
            PlayerAnimation.ARM_SWING.play(citizensNpc.entity as Player)
        }
    }

}