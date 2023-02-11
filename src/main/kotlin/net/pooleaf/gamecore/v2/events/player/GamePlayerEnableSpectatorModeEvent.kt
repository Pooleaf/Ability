package net.pooleaf.gamecore.v2.events.player

import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent
import net.pooleaf.gamecore.v2.player.GamePlayer

/**
 * 플레이어 관전 모드 활성화 시 호출됩니다.
 */
class GamePlayerEnableSpectatorModeEvent(val gamePlayer: GamePlayer): HandlerEvent() {
}