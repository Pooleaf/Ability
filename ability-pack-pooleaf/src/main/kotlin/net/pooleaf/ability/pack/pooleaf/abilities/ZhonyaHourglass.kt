package net.pooleaf.ability.pack.pooleaf.abilities

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.Durationable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack

class ZhonyaHourglass : Ability(), Listener, CastByItemHandler, Cooldownable, Durationable {

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "존야의 모래시계"
        rank = AbilityRank.B
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 우클릭 시 3초간 무적 상태가 되지만 움직일 수 없습니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 90_000L)
    override val durationTimer: DurationTimer = object : DurationTimer(this, 3_000L, 200L) {
        override fun onStart() {
            super.onStart()

            BukkitSyncScope.launch {
                val location = abilityPlayer?.player?.location ?: return@launch
                playStasisSound(location)
                spawnStasisParticles(location)
            }
        }

        override fun onRun() {
            super.onRun()

            BukkitSyncScope.launch {
                spawnStasisParticles(abilityPlayer?.player?.location ?: return@launch)
            }
        }
    }


    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        return clickType == CastByItemHandler.ClickType.RIGHT
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDamage(event: EntityDamageEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player != event.entity || !durationTimer.isRunning) return

        event.isCancelled = true
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (abilityPlayer?.player != event.player || !durationTimer.isRunning) return

        val to = event.to
        event.to = event.from.clone().apply {
            yaw = to.yaw
            pitch = to.pitch
        }
    }

    private fun playStasisSound(location: Location) {
        location.world.playSound(location, XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 0.8F, 0.55F)
        location.world.playSound(location, XSound.ENTITY_ITEM_BREAK.parseSound(), 0.45F, 0.65F)
    }

    private fun spawnStasisParticles(location: Location) {
        val center = location.clone().add(0.0, 1.0, 0.0)

        Particle.SPELL_INSTANT.spawn(center, 0.35F, 16)
        Particle.RED_DUST.spawn(center, 0.35F, 12)
    }

}
