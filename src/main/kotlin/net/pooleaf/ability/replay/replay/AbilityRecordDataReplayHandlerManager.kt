package net.pooleaf.ability.replay.replay

import net.pooleaf.ability.replay.data.ability.*
import net.pooleaf.ability.replay.data.game.AbilityDrawCompleteData
import net.pooleaf.ability.replay.data.game.AbilityDrawCompleteDataReplayHandler
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