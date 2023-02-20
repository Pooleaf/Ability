package net.pooleaf.gamecore.events

import net.pooleaf.core.modules.eventsupport.bukkit.events.CancellableEvent
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.supply.SupplyBlock

class SupplyGetEvent(
    val gamePlayer: GamePlayer,
    val supplyBlock: SupplyBlock
) : CancellableEvent() {
}