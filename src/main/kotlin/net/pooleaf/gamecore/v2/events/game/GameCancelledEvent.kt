package net.pooleaf.gamecore.v2.events.game

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import org.bukkit.command.CommandSender

class GameCancelledEvent(
    cancelSender: CommandSender?,
    cancelCause: String
): HandlerEvent() {
}