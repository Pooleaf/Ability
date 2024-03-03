package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

/**
 * 데미지 증가하는 능력에게 맞을 경우
 * 데미지가 증가되지 않는 오류 수정
 */
class Zombie : Ability(), Listener {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "좀비"
        rank = AbilityRank.B
        type = AbilityType.PASSIVE
        description = listOf(
            "모든 데미지의 반을 흡수합니다.",
            "폭발 데미지를 4배로 받습니다.",
            "불 공격의 데미지를 8배로 받습니다.",
        )

        ban = false
    }


    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDamage(event: EntityDamageEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player != event.entity) return

        when (event.cause) {
            // 불 데미지 8배
            EntityDamageEvent.DamageCause.LAVA,
            EntityDamageEvent.DamageCause.FIRE,
            EntityDamageEvent.DamageCause.FIRE_TICK -> {
                event.damage *= 8
            }

            // 폭발 데미지 4배
            EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION -> {
                event.damage *= 4
            }

            // 나머지 데미지 0.5배
            else -> {
                event.damage /= 2
            }
        }
    }

}