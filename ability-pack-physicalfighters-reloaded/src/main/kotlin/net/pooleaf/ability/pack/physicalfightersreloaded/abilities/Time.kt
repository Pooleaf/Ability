package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.*
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack

/**
 * 기존에는 PlayerMoveEvent를 cancel시키는 식이라 화면이 어지러워 다시 만듦
 */
class Time : Ability(), Listener, CastByItemHandler, Cooldownable, Durationable {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "타임"
        rank = AbilityRank.S
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 자신을 제외한 모든 플레이어의 이동을 5초동안 차단합니다.",
            "단, 직접적인 이동만 불가능합니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 40_000L)
    override val durationTimer: DurationTimer = object : DurationTimer(this, 5_000L) {
        override fun onStart() {
            super.onStart()

            BukkitBroadcaster.broadcast("§f${abilityPlayer?.displayName} §e님께서 타임 능력을 사용했습니다.")
        }

        override fun onEnd() {
            super.onEnd()

            BukkitBroadcaster.broadcast("§e타임 능력이 해제되었습니다.")
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
    fun onMove(event: PlayerMoveEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player == event.player || !durationTimer.isRunning) return

        val gamePlayer = AbilityApi.unsafe.playerManager.get(event.player.uniqueId) ?: return
        if (!gamePlayer.isPlaying()) return

        event.to = event.from
    }

}