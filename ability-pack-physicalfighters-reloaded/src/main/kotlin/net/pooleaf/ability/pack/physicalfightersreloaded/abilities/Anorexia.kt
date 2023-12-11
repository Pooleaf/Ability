package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.FoodLevelChangeEvent

class Anorexia : Ability(), Listener {

    init {
        pluginName = AbilityPlugin.instance.name

        name = "거식증"
        rank = AbilityRank.A
        type = AbilityType.PASSIVE
        description = listOf(
            "배고픔이 항상 최대로 고정됩니다.",
            "체력 회복량이 3배로 증가합니다.",
        )

        ban = false
    }


    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (player?.player != event.entity) return

        event.foodLevel = 20
        (event.entity as Player).saturation = 0f
    }

    @EventHandler
    fun onRegainHealth(event: EntityRegainHealthEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.entity) return

        event.amount *= 3
    }

}