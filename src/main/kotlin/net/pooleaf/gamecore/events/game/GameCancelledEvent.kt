package net.pooleaf.gamecore.events.game

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import org.bukkit.command.CommandSender

class GameCancelledEvent(
    cancelSender: CommandSender?,
    cancelCause: String
): HandlerEvent() {
}