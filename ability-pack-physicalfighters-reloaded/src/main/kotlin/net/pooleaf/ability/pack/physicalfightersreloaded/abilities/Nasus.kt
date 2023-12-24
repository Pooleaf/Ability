package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

class Nasus : Ability(), Listener, Cooldownable {

    private var isReceiveAbilityItem = false
    private var stack = 0

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "나서스"
        rank = AbilityRank.C
        type = AbilityType.ACTIVE
        description = listOf(
            "괭이로 흙을 경작할 때마다 1 스택이 증가합니다.",
            "괭이의 피해량이 10 스택 당 1 증가합니다.",
            "최대 스택: 300"
        )

        ban = false
    }

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 3_000L)


    override fun onAssign() {
        giveItem()
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.player) return

        if (!isReceiveAbilityItem) {
            giveItem()
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.player || cooldownTimer.isRunning) return
        if (player?.player?.itemInHand == null || event.action != Action.RIGHT_CLICK_BLOCK || event.clickedBlock.type != Material.DIRT && event.clickedBlock.type != Material.GRASS) return

        val hand = player!!.player.itemInHand.type
        if (!(hand == Material.WOOD_HOE || hand == Material.STONE_HOE || hand == Material.GOLD_HOE || hand == Material.IRON_HOE || hand == Material.DIAMOND_HOE)) return

        if (stack >= 300) {
            player?.sendWarningSafely("최대 스택인 300 스택에 도달하여 더 이상 스택을 쌓을 수 없습니다.")
            return
        }

        stack++
        player?.sendMessageSafely("§f1 §e스택이 증가했습니다. (현재 스택: §f${stack}§e)")

        cooldownTimer.start()
    }

    @EventHandler
    fun onHit(event: EntityDamageByEntityEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.damager) return
        if (player?.player?.itemInHand == null) return

        val hand = player!!.player.itemInHand.type
        if (!(hand == Material.WOOD_HOE || hand == Material.STONE_HOE || hand == Material.GOLD_HOE || hand == Material.IRON_HOE || hand == Material.DIAMOND_HOE)) return

        event.damage += stack / 10
    }

    private fun giveItem() {
        if (player?.player != null) {
            player?.player!!.inventory.addItem(ItemStack(Material.WOOD_HOE))
            isReceiveAbilityItem = true
        }
    }

}