package net.pooleaf.gamecore.v2.events.player

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.v2.player.GamePlayer

/**
 * 플레이어가 게임에 참여할 때 호출됩니다.
 */
class GamePlayerJoinToGameEvent(val gamePlayer: GamePlayer): HandlerEvent() {
}