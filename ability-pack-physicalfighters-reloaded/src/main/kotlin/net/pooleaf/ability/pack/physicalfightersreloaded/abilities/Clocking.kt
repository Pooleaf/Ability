package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import kotlinx.coroutines.launch
import net.pooleaf.ability.ability.*
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

class Clocking : Ability(), Listener, CastByItemHandler, Cooldownable, Durationable {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

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

            BukkitSyncScope.launch {
                if (abilityPlayer?.player == null) return@launch
                Bukkit.getOnlinePlayers().forEach { it.hidePlayer(abilityPlayer?.player) }
            }
        }

        override fun onEnd() {
            super.onEnd()

            BukkitSyncScope.launch {
                if (abilityPlayer?.player == null) return@launch
                Bukkit.getOnlinePlayers().forEach { it.showPlayer(abilityPlayer?.player) }
            }
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
        if (abilityPlayer?.player == null) return

        if (durationTimer.isRunning) {
            event.player.hidePlayer(abilityPlayer?.player)
        } else {
            event.player.showPlayer(abilityPlayer?.player)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (abilityPlayer?.player == null) return

        event.player.showPlayer(abilityPlayer?.player)
    }

}