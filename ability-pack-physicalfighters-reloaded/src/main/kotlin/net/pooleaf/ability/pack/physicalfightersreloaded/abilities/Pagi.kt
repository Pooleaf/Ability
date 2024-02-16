package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import kotlinx.coroutines.launch
import net.pooleaf.ability.ability.*
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.utils.damageBypassAntiCheat
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Async 오류 수정
 * 설명 오류 수정 (5초마다 -> 1.5초마다)
 * 지속시간 너프 (20초 -> 10초)
 */
class Pagi : Ability(), CastByItemHandler, Cooldownable, Durationable {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "패기"
        rank = AbilityRank.SS
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 10칸 안에 있는 적에게 10초 동안 1.5초마다 5 데미지를 줍니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 140_000L)
    override val durationTimer: DurationTimer = object : DurationTimer(this, 10_000L) {
        override fun onRun() {
            super.onRun()

            BukkitSyncScope.launch {
                val remainingTimeMillis = remainingTimeMillis ?: return@launch
                val remainingTime100Millis = Math.round(remainingTimeMillis.toFloat() / 100)
                if (remainingTimeMillis != null && remainingTime100Millis % 15 == 0) {
                    damageNearPlayers()
                }
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

    private fun damageNearPlayers() {
        val abilityPlayer = player ?: return

        GameCore.unsafe.playerManager.getOnlinePlayingPlayers()
            .filter { abilityPlayer.team != it.team }
            .filter { abilityPlayer.player.location.distance(it.player.player.location) <= 10 }
            .forEach {
                it.player.damageBypassAntiCheat(5.0, abilityPlayer.player)
                it.player.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 30, 0), true)
            }
    }

}