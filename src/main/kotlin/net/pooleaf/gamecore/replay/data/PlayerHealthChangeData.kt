package net.pooleaf.gamecore.replay.data

import com.google.gson.annotations.Expose
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.replay.RecordTickEvent
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

class PlayerHealthChangeData : RecordData {

    override val type: String = "healthChange"

    lateinit var playerUuid: UUID
    var health: Double = 0.0

    override fun onPlay(replayPlayer: ReplayPlayer) {
        val replayNpc = replayPlayer.npcs.get(playerUuid) ?: return

        replayNpc.health = health
    }

}

class PlayerHealthChangeDataListener : Listener {

    @Expose
    private val beforeHealths = hashMapOf<UUID, Double>()

    @EventHandler
    fun onHeathChange(event: RecordTickEvent) {
        event.record.recordTargetPlayers.forEach { uuid ->
            val player = Bukkit.getPlayer(uuid)
            if (player == null) return@forEach

            val beforeHealth = beforeHealths.getOrDefault(player.uniqueId, 0.0)
            val currentHealth = player.health

            if (beforeHealth != currentHealth) {
                val recordData = PlayerHealthChangeData().apply {
                    playerUuid = player.uniqueId
                    health = currentHealth
                }
                GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
            }

            beforeHealths.put(player.uniqueId, currentHealth)
        }
    }

}