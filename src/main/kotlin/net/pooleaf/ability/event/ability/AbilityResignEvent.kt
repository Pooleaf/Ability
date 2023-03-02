package net.pooleaf.ability.event.ability

import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent

/**
 * 능력 할당 해제 이벤트
 */
class AbilityResignEvent(
    val abilityPlayer: AbilityPlayer,
    val ability: Ability
) : HandlerEvent() {
}