package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.utils.damageBypassAntiCheat
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

/**
 * 공격 핵방지 우회
 */
class ExplosionGlove : Ability(), CastByItemHandler, Cooldownable {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "폭파장갑"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 폭발을 일으켜 주변 플레이어들에게 데미지를 주며, 공중으로 띄웁니다."
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 50_000L)


    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        val player = playerInteractEvent.player
        player.world.createExplosion(player.location, 5.0F)

        GameCore.unsafe.playerManager.getOnlinePlayingPlayers()
            .filter { it != abilityPlayer && it.player.location.distance(player.location) <= 15 }
            .forEach {
                it.player.location.world.createExplosion(it.player.location, 3.0F)
                it.player.damageBypassAntiCheat(14.0, player)
                it.player.velocity = Vector(0.0, 1.5, 0.0)
            }

        return true
    }

}