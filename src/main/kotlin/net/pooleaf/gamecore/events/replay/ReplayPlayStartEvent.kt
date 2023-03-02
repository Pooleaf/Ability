package net.pooleaf.gamecore.events.replay

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.replay.replay.ReplayPlayer

/**
 * 리플레이 재상 시작 이벤트
 */
class ReplayPlayStartEvent(val replayPlayer: ReplayPlayer) : HandlerEvent() {
}