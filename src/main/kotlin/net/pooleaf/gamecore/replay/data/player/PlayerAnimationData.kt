package net.pooleaf.gamecore.replay.data.player

import net.citizensnpcs.util.PlayerAnimation
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerAnimationType
import java.util.*

class PlayerAnimationData : RecordData {

    override val type: String = "playerAnimation"

    lateinit var playerUuid: UUID
    lateinit var animationType: String


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val citizensNpc = replayPlayer.npcs.get(playerUuid)?.citizensNpc ?: return

        val animationType = PlayerAnimationType.valueOf(animationType)
        if (animationType == PlayerAnimationType.ARM_SWING) {
            PlayerAnimation.ARM_SWING.play(citizensNpc.entity as Player)
        }
    }

}

class PlayerAnimationDataListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerAnimation(event: PlayerAnimationEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val player = event.player
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(player)) return

        val record = GameCore.unsafe.recordManager.record!!
        val recordData = PlayerAnimationData().apply {
            playerUuid = player.uniqueId
            animationType = event.animationType.name
        }
        record.addRecordData(recordData)
    }

}