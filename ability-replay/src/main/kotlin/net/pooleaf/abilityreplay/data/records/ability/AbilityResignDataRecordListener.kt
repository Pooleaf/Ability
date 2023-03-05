package net.pooleaf.abilityreplay.data.records.ability

import net.pooleaf.ability.event.ability.AbilityResignEvent
import net.pooleaf.abilityreplay.data.datas.ability.AbilityResignData
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityResignDataRecordListener : Listener {

    @EventHandler
    fun onAbilityResign(event: AbilityResignEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = AbilityResignData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}