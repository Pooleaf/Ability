package net.pooleaf.abilityreplay.data.records.ability

import net.pooleaf.ability.event.ability.AbilityCooldownStartEvent
import net.pooleaf.abilityreplay.data.datas.ability.AbilityCooldownStartData
import net.pooleaf.gamereplay.GameReplayApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AbilityCooldownStartDataRecordListener : Listener {

    @EventHandler
    fun onAbilityCooldownStart(event: AbilityCooldownStartEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = AbilityCooldownStartData().apply {
            playerUuid = event.abilityPlayer.uuid
            abilityName = event.ability.name
            cooldownMillis = event.cooldownMillis
        }
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}