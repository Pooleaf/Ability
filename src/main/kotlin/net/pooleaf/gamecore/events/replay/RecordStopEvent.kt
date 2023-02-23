package net.pooleaf.gamecore.events.replay

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.replay.record.Record

/**
 * 리플레이 녹화 종료 후 실행됩니다.
 */
class RecordStopEvent(val record: Record) : HandlerEvent() {
}