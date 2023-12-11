package net.pooleaf.ability.abilities

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class TestActiveAbility: Ability(), CastByItemHandler {

    init {
        pluginName = AbilityPlugin.instance.name

        name = "테스트 액티브"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf("철괴 우클릭 시 보고있는 지점에 번개가 칩니다.")

        ban = true
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 5000L)

    // 사거리
    val targetDistance = 100

    // 범위
    val rangeRadius = 1.5

    var rangeShowJob: Job? = null


    override fun onAssign() {
        rangeShowJob = BukkitSyncScope.launch {
            while (true) {
                if (player == null) {
                    cancel()
                    return@launch
                }

                if (player?.isOnline == false) {
                    return@launch
                }

                getTargetLocation()?.let { location ->
                    for (i in 0 until 360 step 10) {
                        val offsetX = Math.cos(Math.toRadians(i.toDouble())) * rangeRadius
                        val offsetZ = Math.sin(Math.toRadians(i.toDouble())) * rangeRadius

                        if (isCastItem(player!!.player!!.itemInHand)) {
                            Particle.SPELL_INSTANT.spawn(player!!.player, Location(location.world, location.x + offsetX, location.y + 1, location.z + offsetZ), 0F, 1)
                        }
                    }
                }

                delay(1000 / 20 * 2)
            }
        }
    }

    override fun onResign() {
        rangeShowJob?.let {
            it.cancel()
        }
    }

    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        if (clickType != CastByItemHandler.ClickType.RIGHT) return false

        getTargetLocation()?.let { location ->
            location.world.strikeLightning(location)
        }

        return true
    }

    fun getTargetLocation(): Location? {
        player?.player?.let { player ->
            return player.getTargetBlock(null as Set<Material>?, targetDistance).location
        }

        return null
    }

}