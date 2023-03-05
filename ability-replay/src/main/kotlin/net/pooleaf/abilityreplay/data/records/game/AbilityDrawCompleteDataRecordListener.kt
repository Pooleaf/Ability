package net.pooleaf.abilityreplay.data.records.game

import net.pooleaf.ability.event.game.AbilityDrawCompleteEvent
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityDrawCompleteDataRecordListener : Listener {

    @EventHandler
    fun onAbilityDrawComplete(event: AbilityDrawCompleteEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = net.pooleaf.abilityreplay.data.datas.game.AbilityDrawCompleteData()
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}