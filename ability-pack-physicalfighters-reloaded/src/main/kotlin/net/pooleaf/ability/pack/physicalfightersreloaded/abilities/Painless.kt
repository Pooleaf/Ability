package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerVelocityEvent

class Painless : Ability(), Listener {

    private var damaged1 = false
    private var damaged2 = false

    init {
        pluginName = AbilityPlugin.instance.name

        name = "무통증"
        rank = AbilityRank.B
        type = AbilityType.PASSIVE
        description = listOf(
            "공격 받을 시 80% 확률로 넉백을 무시합니다.",
        )

        ban = false
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.entity) return
        if (Math.random() > 0.8) return

        damaged1 = true
        damaged2 = true
    }

    @EventHandler
    fun onVelocity(event: PlayerVelocityEvent) {
        if (!damaged1 && !damaged2) return

        damaged2 = damaged1
        damaged1 = false

        event.isCancelled = true
    }

}