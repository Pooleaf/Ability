package net.pooleaf.gamecore.v1.events.game

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import org.bukkit.command.CommandSender

class GameCountingStartedEvent(
    val starter: CommandSender?
): HandlerEvent() {

    fun isAutoStart() = (starter == null)

}