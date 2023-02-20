package net.pooleaf.ability.sidebar.elements

import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.core.modules.support.common.util.StringUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.sidebar.GameSideBarNamedTextElement

class DrawSkipElement: GameSideBarNamedTextElement("§6능력 강제 확정까지") {

    override var valueText: String
        get() = (GameCore.game.phasePipeline.currentPhase as AbilityDrawPhase).skipRemainingSeconds?.let { seconds ->
            StringUtil.buildTimeStringFromSeconds(seconds.toLong())
        } ?: ""
        set(value) {}

    override fun getPriority(): Int {
        return 10
    }

    override fun isShow(): Boolean {
        val phase = GameCore.game.phasePipeline.currentPhase
        return phase is AbilityDrawPhase && phase.skipRemainingSeconds != null
    }

    override fun isUseSpace(): Boolean {
        return true
    }

}