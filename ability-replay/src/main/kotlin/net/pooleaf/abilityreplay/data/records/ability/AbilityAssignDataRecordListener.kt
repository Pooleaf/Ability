package net.pooleaf.abilityreplay.data.records.ability

import net.pooleaf.ability.event.ability.AbilityAssignEvent
import net.pooleaf.abilityreplay.data.datas.ability.AbilityAssignData
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityAssignDataRecordListener : Listener {

    @EventHandler
    fun onAbilityAssign(event: AbilityAssignEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = AbilityAssignData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}