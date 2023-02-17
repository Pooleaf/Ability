package net.pooleaf.ability.sidebar

import net.pooleaf.ability.sidebar.elements.AbilityElement
import net.pooleaf.gamecore.sidebar.GameSideBar
import net.pooleaf.gamecore.sidebar.elements.*

class AbilitySideBar: GameSideBar("§e§l능력자") {

    init {
        // 시간
        elements.add(TimeElement())

        // 경계선 축소
        elements.add(WorldBorderRemainingTimeElement())

        // 경계선 축소 중
        elements.add(WorldBorderReducingElement())

        // 능력
        elements.add(AbilityElement())

        // 남은 인원
        elements.add(RemainingPlayerCountElement())

        // 진행 시간
        elements.add(GameTimeElement())
    }

}