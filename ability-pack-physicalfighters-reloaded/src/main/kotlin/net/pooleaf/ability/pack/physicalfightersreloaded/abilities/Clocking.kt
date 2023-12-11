package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.*
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

class Clocking : Ability(), Listener, CastByItemHandler, Cooldownable, Durationable {

    init {
        pluginName = AbilityPlugin.instance.name

        name = "클로킹"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 5초간 투명 상태가 됩니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 60_000L)
    override val durationTimer: DurationTimer = object : DurationTimer(this, 5_000L) {
        override fun onStart() {
            super.onStart()

            if (player?.player == null) return
            Bukkit.getOnlinePlayers().forEach { it.hidePlayer(player?.player) }
        }

        override fun onEnd() {
            super.onEnd()

            if (player?.player == null) return
            Bukkit.getOnlinePlayers().forEach { it.showPlayer(player?.player) }
        }
    }


    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        return true
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (player?.player == null) return

        if (durationTimer.isRunning) {
            event.player.hidePlayer(player?.player)
        } else {
            event.player.showPlayer(player?.player)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (player?.player == null) return

        event.player.showPlayer(player?.player)
    }

}