package net.pooleaf.ability.pack.pooleaf.abilities

import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Repulsion : Ability(), Listener, Cooldownable {

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "반발"
        rank = AbilityRank.B
        type = AbilityType.PASSIVE
        description = listOf(
            "체력이 50% 이하로 내려가면 주변 플레이어를 뒤로 밀어냅니다.",
            "같은 팀 플레이어는 밀려나지 않습니다.",
        )

        ban = false
    }

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 90_000L)


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onDamage(event: EntityDamageEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isEnded || AbilityApi.game.isGodMode) return
        val abilityPlayer = abilityPlayer ?: return
        val player = abilityPlayer.player ?: return
        if (event.entity != player) return
        if (cooldownTimer.isRunning) return
        if (player.health <= player.maxHealth * 0.5) return
        if (player.health - event.finalDamage > player.maxHealth * 0.5) return

        repel()
        cooldownTimer.start()
    }

    private fun repel() {
        val abilityPlayer = abilityPlayer ?: return
        val player = abilityPlayer.player ?: return
        val center = player.location

        player.world.playSound(center, XSound.ENTITY_GENERIC_EXPLODE.parseSound(), 0.65F, 1.35F)
        spawnRepulsionWave(center)

        AbilityApi.unsafe.playerManager.getOnlinePlayingPlayers()
            .filter { it != abilityPlayer }
            .filter { abilityPlayer.team == null || abilityPlayer.team != it.team }
            .filter { it.player.world == center.world }
            .filter { it.player.location.distance(center) <= RANGE }
            .forEach {
                val direction = it.player.location.toVector().subtract(center.toVector())
                if (direction.lengthSquared() == 0.0) {
                    direction.add(Vector(0.0, 0.0, 1.0))
                }

                it.player.velocity = direction.normalize().multiply(KNOCKBACK_POWER).setY(0.45)
            }
    }

    private fun spawnRepulsionWave(center: Location) {
        object : BukkitRunnable() {
            private var radius = 0.8

            override fun run() {
                if (radius > RANGE) {
                    cancel()
                    return
                }

                for (i in 0 until 36) {
                    val angle = 2 * PI * i / 36
                    val location = center.clone().add(cos(angle) * radius, 0.15, sin(angle) * radius)
                    Particle.CLOUD.spawn(location, 0.0F, 1)
                    Particle.SPELL_INSTANT.spawn(location, 0.0F, 1)
                }

                radius += 0.8
            }
        }.runTaskTimer(PooleafAbilityPlugin.instance, 0L, 1L)
    }

    companion object {
        private const val RANGE = 6.0
        private const val KNOCKBACK_POWER = 1.6
    }

}
