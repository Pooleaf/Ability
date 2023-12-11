package net.pooleaf.ability.abilities

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Durationable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class TestActiveContinueAbility: Ability(), CastByItemHandler, Durationable {

    init {
        pluginName = AbilityPlugin.instance.name

        name = "테스트 액티브 지속"
        rank = AbilityRank.S
        type = AbilityType.ACTIVE
        description = listOf("철괴 우클릭 시 5초간 하늘을 날 수 있습니다.")

        ban = true
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 5000L)

    override val durationTimer = object : DurationTimer(this, 5000L) {
        override fun onStart() {
            super.onStart()

            BukkitSyncScope.launch {
                player?.player?.let { player ->
                    player.allowFlight = true
                    player.isFlying = true

                    XSound.BLOCK_WOOL_BREAK.play(player, 0.8F, 1.0F)
                }
            }
        }

        override fun onEnd() {
            super.onEnd()

            BukkitSyncScope.launch {
                player?.player?.let { player ->
                    player.allowFlight = false
                    player.isFlying = false

                    XSound.BLOCK_WOOL_BREAK.play(player, 1.0F, 1.0F)
                }
            }
        }
    }


    override fun onResign() {
        BukkitSyncScope.launch {
            player?.player?.let { player ->
                player.allowFlight = false
                player.isFlying = false
            }
        }
    }

    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        if (clickType != CastByItemHandler.ClickType.RIGHT) return false
        return true
    }

}