package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

class MultiShot : Ability(), Listener, Cooldownable {

    private var isReceiveAbilityItem = false

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "멀티샷"
        rank = AbilityRank.A
        type = AbilityType.PASSIVE
        description = listOf(
            "활 발사 시 여러개의 화살이 날아갑니다.",
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
        if (abilityPlayer?.player != event.player) return

        if (!isReceiveAbilityItem) {
            giveItem()
        }
    }

    @EventHandler
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player != event.entity.shooter || event.entity !is Arrow || cooldownTimer.isRunning) return

        val arrow = event.entity as Arrow
        for (i in 0..9) {
            val copy = (event.entity.shooter as Player).world.spawnArrow(event.entity.location, event.entity.velocity, 1.5F, 10.0F)
            copy.spigot().damage = arrow.spigot().damage
            copy.isCritical = arrow.isCritical
            copy.knockbackStrength = arrow.knockbackStrength
            copy.fireTicks = arrow.fireTicks
            copy.shooter = arrow.shooter
        }

        cooldownTimer.start()
    }

    private fun giveItem() {
        if (abilityPlayer?.player != null) {
            abilityPlayer?.player!!.inventory.addItem(ItemStack(Material.BOW))
            abilityPlayer?.player!!.inventory.addItem(ItemStack(Material.ARROW, 64))
            isReceiveAbilityItem = true
        }
    }

}