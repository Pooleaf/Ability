package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.gamecore.events.player.GamePlayerDefeatEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class Explosion : Ability(), Listener {

    init {
        pluginName = AbilityPlugin.instance.name

        name = "익스플로젼"
        rank = AbilityRank.B
        type = AbilityType.PASSIVE
        description = listOf(
            "사망 시 강력한 폭발을 일으킵니다.",
        )

        ban = false
    }


    @EventHandler
    fun onDeath(event: GamePlayerDefeatEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.gamePlayer.player) return

        val location: Location = event.gamePlayer.player.location
        Bukkit.getScheduler().runTaskLater(PhysicalFightersReloadedPlugin.instance, {
            location.world.createExplosion(location, 8.0F, false)
        }, 1L)
    }

}