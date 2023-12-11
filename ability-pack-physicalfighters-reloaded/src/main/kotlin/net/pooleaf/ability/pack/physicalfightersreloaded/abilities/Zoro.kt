package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * 데미지 상향
 * 기존: 5~9
 */
class Zoro : Ability(), Listener, CastByItemHandler, Cooldownable {

    var damage: Int? = null

    init {
        pluginName = AbilityPlugin.instance.name

        name = "조로"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 검의 피해량이 무작위로 설정됩니다.",
            "능력 사용으로 설정된 검의 피해량은 인챈트의 영향을 받지 않습니다",
            "설정 피해량 범위: 7~15",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 45_000L)


    override fun onCastByItem(
        event: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        damage = Random.nextInt(9) + 7
        event.player.sendMessage("§e검의 피해량이 §f${damage}§e로 설정되었습니다.")

        return true
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.damager) return
        if (player?.player?.itemInHand == null) return

        // 능력 한번도 안썼으면 원래 데미지 사용
        if (damage == null) return

        val handType = player?.player?.itemInHand?.type
        if (!(handType == Material.WOOD_SWORD || handType == Material.STONE_SWORD || handType == Material.GOLD_SWORD || handType == Material.IRON_SWORD || handType == Material.DIAMOND_SWORD)) return

        event.damage = damage!!.toDouble()
    }

}