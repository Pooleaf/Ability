package net.pooleaf.ability.replay.replay

import net.pooleaf.ability.replay.data.ability.*
import net.pooleaf.ability.replay.data.game.AbilityDrawCompleteData
import net.pooleaf.ability.replay.data.game.AbilityDrawCompleteDataReplayHandler
import net.pooleaf.gamecore.GameCore

class AbilityReplayHandlerRegistry {

    fun registerHandlers() {
        // Game
        GameCore.unsafe.recordDataManager.registerRecordData(AbilityDrawCompleteData::class.java, AbilityDrawCompleteDataReplayHandler())

        // Ability
        GameCore.unsafe.recordDataManager.registerRecordData(AbilityAssignData::class.java, AbilityAssignDataReplayHandler())
        GameCore.unsafe.recordDataManager.registerRecordData(AbilityResignData::class.java, AbilityResignDataReplayHandler())
        GameCore.unsafe.recordDataManager.registerRecordData(AbilityCooldownStartData::class.java, AbilityCooldownStartDataReplayHandler())
        GameCore.unsafe.recordDataManager.registerRecordData(AbilityCooldownEndData::class.java, AbilityCooldownEndDataReplayHandler())
        GameCore.unsafe.recordDataManager.registerRecordData(AbilityDurationStartData::class.java, AbilityDurationStartDataReplayHandler())
        GameCore.unsafe.recordDataManager.registerRecordData(AbilityDurationEndData::class.java, AbilityDurationEndDataReplayHandler())
    }

}