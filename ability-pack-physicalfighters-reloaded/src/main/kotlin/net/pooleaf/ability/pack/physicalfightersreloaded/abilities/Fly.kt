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
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

class Fly : Ability(), Listener, CastByItemHandler, Cooldownable, Durationable {

    init {
        pluginName = AbilityPlugin.instance.name

        name = "플라이"
        rank = AbilityRank.S
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 10초간 하늘을 날 수 있습니다.",
            "낙하 데미지를 받지 않습니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 80_000L)
    override val durationTimer: DurationTimer = object : DurationTimer(this, 10_000L) {
        override fun onStart() {
            super.onStart()

            if (player?.player == null) return

            player?.player!!.allowFlight = true
            player?.player!!.isFlying = true
        }

        override fun onEnd() {
            super.onEnd()

            if (player?.player == null) return

            player?.player!!.allowFlight = false
            player?.player!!.isFlying = false
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
    fun onDamage(event: EntityDamageEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.entity || !durationTimer.isRunning) return
        if (event.cause != EntityDamageEvent.DamageCause.FALL) return

        event.isCancelled = true
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (player?.player == null) return

        if (durationTimer.isRunning) {
            player?.player!!.allowFlight = true
            player?.player!!.isFlying = true
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if (player?.player == null) return

        player?.player!!.allowFlight = false
        player?.player!!.isFlying = false
    }

}