package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.*
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class Aegis : Ability(), Listener, CastByItemHandler, Cooldownable, Durationable {

    init {
        pluginName = AbilityPlugin.instance.name

        name = "이지스"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 5초간 무적 상태가 됩니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 55_000L)
    override val durationTimer: DurationTimer = DurationTimer(this, 5_000L)


    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        return true
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.entity || !durationTimer.isRunning) return

        event.entity.fireTicks = 0
        event.isCancelled = true
    }

}