package net.pooleaf.gamecore.events.game

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import org.bukkit.command.CommandSender

class GameStartedEvent(
    val starter: CommandSender?
): HandlerEvent() {

    fun isAutoStart() = (starter == null)

}