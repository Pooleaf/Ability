package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.core.modules.commonsender.bukkit.BukkitPlayer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask
import kotlin.math.min

/**
 * Yeomryo.class
 * 오버밸런스라 밸런스 조정 용도로 다시 만듦
 * 기존: 공격 시 3 추가 데미지, 보호막 30, 5초당 10 회복
 */
class Yasuo : Ability(), Listener {

    var task: BukkitTask? = null

    init {
        pluginName = AbilityPlugin.instance.name

        name = "야스오"
        rank = AbilityRank.A
        type = AbilityType.PASSIVE
        description = listOf(
            "공격 시 2의 추가 데미지를 입힙니다.",
            "데미지를 흡수하는 6의 보호막을 얻습니다.",
            "보호막은 10초마다 2씩 회복됩니다.",
        )

        ban = false
    }


    override fun onAssign() {
        (player?.commonPlayer as BukkitPlayer).absorptionHearts = 6.0F

        task = Bukkit.getScheduler().runTaskTimerAsynchronously(PhysicalFightersReloadedPlugin.instance, {
            val player = (player?.commonPlayer as BukkitPlayer)
            if (player.absorptionHearts >= 6.0F) return@runTaskTimerAsynchronously

            player.absorptionHearts = min(6.0, player.absorptionHearts + 2.0).toFloat()
        }, 10 * 20L, 10 * 20L)
    }

    override fun onResign() {
        (player?.commonPlayer as BukkitPlayer).absorptionHearts = 0.0F
        task?.cancel()
    }

    @EventHandler
    fun onHit(event: EntityDamageByEntityEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.damager) return

        event.damage += 2
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.player) return

        (player?.commonPlayer as BukkitPlayer).absorptionHearts = 0.0F
    }

}