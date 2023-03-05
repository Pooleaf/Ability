package net.pooleaf.ability.event.ability

import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.core.modules.eventsupport.bukkit.events.HandlerEvent

/**
 * 능력 쿨타임 시작 이벤트
 */
class AbilityCooldownStartEvent(
    val abilityPlayer: AbilityPlayer,
    val ability: Ability,
    val cooldownMillis: Long
) : HandlerEvent() {
}