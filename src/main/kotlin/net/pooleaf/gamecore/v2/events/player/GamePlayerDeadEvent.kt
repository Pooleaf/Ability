package net.pooleaf.gamecore.v2.events.player

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.v2.player.GamePlayer
import org.bukkit.event.entity.PlayerDeathEvent

/**
 * 플레이어 사망 시 호출됩니다.
 */
class GamePlayerDeadEvent(
    val deadGamePlayer: GamePlayer,
    val killerGamePlayer: GamePlayer?,
    val playerDeathEvent: PlayerDeathEvent
): HandlerEvent() {
}