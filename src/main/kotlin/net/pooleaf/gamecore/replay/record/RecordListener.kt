package net.pooleaf.gamecore.replay.record

import net.pooleaf.gamecore.GameCore
import org.bukkit.entity.Player
import org.bukkit.event.Listener

interface RecordListener : Listener {

    fun isRecording(): Boolean {
        return GameCore.unsafe.recordManager.isRecording()
    }

    fun isRecordingTargetPlayer(player: Player): Boolean {
        return GameCore.unsafe.recordManager.record!!.recordTargetPlayers.contains(player.uniqueId)
    }

}