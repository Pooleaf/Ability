package net.pooleaf.gamecore.events.replay

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.replay.replay.ReplayPlayer

class ReplayExitEvent(val replayPlayer: ReplayPlayer) : HandlerEvent() {
}