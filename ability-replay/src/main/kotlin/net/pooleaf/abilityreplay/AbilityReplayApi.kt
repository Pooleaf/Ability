package net.pooleaf.abilityreplay

import net.pooleaf.abilityreplay.replay.AbilityReplayHandlerRegistry

object AbilityReplayApi {

    object unsafe {

        lateinit var abilityReplayHandlerRegistry: AbilityReplayHandlerRegistry

        fun init() {
            abilityReplayHandlerRegistry = AbilityReplayHandlerRegistry()

            abilityReplayHandlerRegistry.registerHandlers()
        }
    }


    fun init() {
        unsafe.init()
    }

}