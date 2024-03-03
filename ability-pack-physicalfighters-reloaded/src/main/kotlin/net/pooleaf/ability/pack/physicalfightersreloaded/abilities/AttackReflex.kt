package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.*
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.gamecore.utils.damageBypassAntiCheat
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class AttackReflex : Ability(), Listener, CastByItemHandler, Cooldownable, Durationable {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "공격반사"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 5초간 받는 데미지를 반사합니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 60_000L)
    override val durationTimer: DurationTimer = DurationTimer(this, 5_000L)


    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        BukkitBroadcaster.broadcast("§e지금부터 5초간 §f${abilityPlayer?.displayName} §e님에게 가한 데미지가 반사됩니다.")
        return true
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        val player = abilityPlayer?.player

        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player == null || player != event.entity || !durationTimer.isRunning || event.damager !is LivingEntity) return

        (event.damager as LivingEntity).damageBypassAntiCheat(event.damage, player)
        event.isCancelled = true
    }

}