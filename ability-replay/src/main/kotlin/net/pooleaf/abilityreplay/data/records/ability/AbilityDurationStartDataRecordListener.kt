package net.pooleaf.abilityreplay.data.records.ability

import net.pooleaf.ability.event.ability.AbilityDurationStartEvent
import net.pooleaf.abilityreplay.data.datas.ability.AbilityDurationStartData
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityDurationStartDataRecordListener : Listener {

    @EventHandler
    fun onAbilityDurationStart(event: AbilityDurationStartEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = AbilityDurationStartData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
            durationMillis = event.durationMillis
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}