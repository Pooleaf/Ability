package net.pooleaf.ability.pack.pooleaf.abilities

import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class FingerSnap : Ability(), CastByItemHandler, Cooldownable {

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "핑거스냅"
        rank = AbilityRank.HIDDEN
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 우클릭 시 생존한 플레이어 중 절반을 사망시킵니다.",
            "자신도 사망할 수 있으며, 살아남으면 나약함 효과를 받습니다.",
            "한 번만 사용할 수 있습니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 999_999L)


    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        if (clickType != CastByItemHandler.ClickType.RIGHT) return false

        snap()
        return true
    }

    private fun snap() {
        val abilityPlayer = abilityPlayer ?: return
        val playingPlayers = AbilityApi.unsafe.playerManager.getPlayingPlayers()
        val defeatedPlayers = playingPlayers.shuffled().take(playingPlayers.size / 2)

        BukkitBroadcaster.broadcast("§e손가락이 튕겨졌습니다.")

        BukkitAsyncScope.launch {
            defeatedPlayers.forEach {
                if (it.isPlaying()) {
                    it.defeat()
                }
            }

            if (!defeatedPlayers.contains(abilityPlayer) && abilityPlayer.isPlaying()) {
                BukkitSyncScope.launch {
                    abilityPlayer.player?.addPotionEffect(
                        PotionEffect(PotionEffectType.WEAKNESS, 100_000, 0),
                        true
                    )
                }
            }
        }
    }

}
