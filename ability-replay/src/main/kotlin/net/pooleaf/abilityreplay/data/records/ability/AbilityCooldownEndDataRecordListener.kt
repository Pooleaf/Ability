package net.pooleaf.abilityreplay.data.records.ability

import net.pooleaf.ability.event.ability.AbilityCooldownEndEvent
import net.pooleaf.abilityreplay.data.datas.ability.AbilityCooldownEndData
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityCooldownEndDataRecordListener : Listener {

    @EventHandler
    fun onAbilityCooldownEnd(event: AbilityCooldownEndEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = AbilityCooldownEndData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}