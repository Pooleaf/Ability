package net.pooleaf.gamecore.v1.events.game

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.v2.team.Team

/**
 * 게임 종료 시 호출됩니다.
 */
class GameEndEvent(
    val winnerTeam: Team?
): HandlerEvent() {
}