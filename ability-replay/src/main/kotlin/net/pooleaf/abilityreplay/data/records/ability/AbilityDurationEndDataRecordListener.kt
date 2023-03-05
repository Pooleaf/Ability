package net.pooleaf.abilityreplay.data.records.ability

import net.pooleaf.ability.event.ability.AbilityDurationEndEvent
import net.pooleaf.abilityreplay.data.datas.ability.AbilityDurationEndData
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityDurationEndDataRecordListener : Listener {

    @EventHandler
    fun onAbilityDurationEnd(event: AbilityDurationEndEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = AbilityDurationEndData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}