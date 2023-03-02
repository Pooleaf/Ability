package net.pooleaf.ability.replay.replay

import net.pooleaf.ability.replay.data.ability.*
import net.pooleaf.ability.replay.data.game.AbilityDrawCompleteData
import net.pooleaf.ability.replay.data.game.AbilityDrawCompleteDataReplayHandler
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.block.BlockBreakDataReplayHandler

class AbilityReplayHandlerRegistry {

    fun registerHandlers() {
        // Game
        GameCore.unsafe.recordDataReplayHandlerManager.set(AbilityDrawCompleteData::class.java, AbilityDrawCompleteDataReplayHandler())

        // Ability
        GameCore.unsafe.recordDataReplayHandlerManager.set(AbilityAssignData::class.java, AbilityAssignDataReplayHandler())
        GameCore.unsafe.recordDataReplayHandlerManager.set(AbilityResignData::class.java, AbilityResignDataReplayHandler())
        GameCore.unsafe.recordDataReplayHandlerManager.set(AbilityCooldownStartData::class.java, AbilityCooldownStartDataReplayHandler())
        GameCore.unsafe.recordDataReplayHandlerManager.set(AbilityCooldownEndData::class.java, AbilityCooldownEndDataReplayHandler())
        GameCore.unsafe.recordDataReplayHandlerManager.set(AbilityDurationStartData::class.java, AbilityDurationStartDataReplayHandler())
        GameCore.unsafe.recordDataReplayHandlerManager.set(AbilityDurationEndData::class.java, AbilityDurationEndDataReplayHandler())
    }

}