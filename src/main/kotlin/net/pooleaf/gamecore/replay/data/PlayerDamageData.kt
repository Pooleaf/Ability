package net.pooleaf.gamecore.replay.data

import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageEvent
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import java.util.*

class PlayerDamageData : RecordData, Listener {

    override val type: String = "playerDamage"

    lateinit var playerUuid: UUID


    override fun play(replayPlayer: ReplayPlayer) {
        TODO("Not yet implemented")
    }

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