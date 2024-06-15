package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.*
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.gamecore.utils.damageBypassAntiCheat
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.cos
import kotlin.math.sin

/**
 * 공격 핵방지 우회
 */
class ExplosionPa : Ability(), Listener, CastByItemHandler, Cooldownable, Durationable {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "기공파"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 바라보는 방향으로 5초 동안 강한 폭발을 발사합니다.",
            "능력 시전 중에는 움직일 수 없습니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 40_000L)
    override val durationTimer: DurationTimer = object : DurationTimer(this, 5_000L) {

        private var fireJob: Job? = null

        override fun onStart() {
            super.onStart()

            fireJob = BukkitSyncScope.launch {
                for (i in 0..9) {
                    fireExplosion(i)
                    delay(500L)
                }
            }
        }

        override fun onCancel() {
            fireJob?.cancel()
        }

        private fun fireExplosion(distance: Int) {
            val player = abilityPlayer?.player ?: return

            val playerLocation = player.location
            val explosionLocation = player.location

            val degreeV = Math.toRadians(-(playerLocation.yaw % 360.0))
            val degreeH = Math.toRadians(-(playerLocation.pitch % 360.0))

            explosionLocation.x = playerLocation.x + (distance * 2 + 2) * sin(degreeV) * cos(degreeH)
            explosionLocation.y = playerLocation.y + (distance * 2 + 2) * sin(degreeH)
            explosionLocation.z = playerLocation.z + (distance * 2 + 2) * cos(degreeV) * cos(degreeH)
            explosionLocation.world.createExplosion(explosionLocation, 0.0F)

            AbilityApi.unsafe.playerManager.getOnlinePlayingPlayers()
                .filter { it.uuid != abilityPlayer?.uuid }
                .forEach {
                    val playingPlayer = it.player ?: return@forEach
                    if (explosionLocation.distance(playingPlayer.location) <= 4.0) {
                        playingPlayer.damageBypassAntiCheat(10.0, player)
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

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (event.player != abilityPlayer?.player) return
        if (durationTimer.isRunning) {
            event.to = event.from
        }
    }

}