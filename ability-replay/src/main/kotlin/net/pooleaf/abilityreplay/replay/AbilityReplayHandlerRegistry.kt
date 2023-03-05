package net.pooleaf.abilityreplay.replay

import net.pooleaf.abilityreplay.data.datas.ability.*
import net.pooleaf.abilityreplay.data.datas.game.AbilityDrawCompleteData
import net.pooleaf.abilityreplay.data.replays.ability.*
import net.pooleaf.abilityreplay.data.replays.game.AbilityDrawCompleteDataReplayHandler
import net.pooleaf.gamereplay.GameReplayApi

class AbilityReplayHandlerRegistry {

    fun registerHandlers() {
        // Game
        GameReplayApi.unsafe.recordDataManager.registerRecordData(AbilityDrawCompleteData::class.java, AbilityDrawCompleteDataReplayHandler())

        // Ability
        GameReplayApi.unsafe.recordDataManager.registerRecordData(AbilityAssignData::class.java, AbilityAssignDataReplayHandler())
        GameReplayApi.unsafe.recordDataManager.registerRecordData(AbilityResignData::class.java, AbilityResignDataReplayHandler())
        GameReplayApi.unsafe.recordDataManager.registerRecordData(AbilityCooldownStartData::class.java, AbilityCooldownStartDataReplayHandler())
        GameReplayApi.unsafe.recordDataManager.registerRecordData(AbilityCooldownEndData::class.java, AbilityCooldownEndDataReplayHandler())
        GameReplayApi.unsafe.recordDataManager.registerRecordData(AbilityDurationStartData::class.java, AbilityDurationStartDataReplayHandler())
        GameReplayApi.unsafe.recordDataManager.registerRecordData(AbilityDurationEndData::class.java, AbilityDurationEndDataReplayHandler())
    }

}