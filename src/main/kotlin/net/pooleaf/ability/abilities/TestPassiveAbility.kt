package net.pooleaf.ability.abilities

import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.core.modules.eventsupport.bukkit.events.damage.PlayerDamageByEntityEvent
import org.bukkit.event.Listener

class TestPassiveAbility: Ability(), Listener {

    init {
        pluginName = AbilityPlugin.instance.name

        name = "테스트 패시브"
        rank = AbilityRank.C
        type = AbilityType.PASSIVE
        description = listOf("공격을 받으면 상대에게 번개가 칩니다.")

        ban = false
    }


    fun onPlayerDamageByEntity(event: PlayerDamageByEntityEvent) {
        player?.player?.let { player ->
            // 플레이어 체크
            if (!event.player.equals(player)) return

            event.damager.world.strikeLightning(event.damager.location)
        }
    }
}