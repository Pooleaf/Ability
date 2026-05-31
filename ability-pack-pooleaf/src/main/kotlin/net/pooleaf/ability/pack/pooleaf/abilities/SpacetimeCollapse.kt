package net.pooleaf.ability.pack.pooleaf.abilities

import com.cryptomorin.xseries.XSound
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.NPC
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.event.ability.AbilityCooldownEndEvent
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import net.pooleaf.gamecore.utils.damageBypassAntiCheat
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import java.util.ArrayDeque
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class SpacetimeCollapse : Ability(), Listener, CastByItemHandler, Cooldownable {

    private val records = ArrayDeque<Record>()
    private var recordTask: BukkitTask? = null
    private var followTask: BukkitTask? = null
    private var rewindTask: BukkitTask? = null
    private var shadow: NPC? = null
    private var isRewinding = false

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "시공간 붕괴"
        rank = AbilityRank.SS
        type = AbilityType.ACTIVE
        description = listOf(
            "6초 전의 나를 따라하는 그림자가 생성됩니다.",
            "철괴 우클릭 시 그림자(6초 전)의 위치와 체력으로 돌아갑니다.",
            "돌아간 위치에 10 광역 데미지를 입힙니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 150_000L)


    override fun onAssign() {
        startRecording()
        spawnShadow()
    }

    override fun onResign() {
        stopRecording()
        stopFollow()
        stopRewind()
        removeShadow()
    }

    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        if (clickType != CastByItemHandler.ClickType.RIGHT) return false
        if (isRewinding || records.isEmpty()) return false

        startRewind()
        return true
    }

    // 내 능력의 쿨타임이 끝나면 그림자를 다시 생성한다.
    @EventHandler
    fun onCooldownEnd(event: AbilityCooldownEndEvent) {
        if (event.ability != this) return
        if (event.abilityPlayer.uuid != abilityPlayer?.uuid) return

        spawnShadow()
    }

    // ----- 기록 -----

    private fun startRecording() {
        stopRecording()
        recordTask = Bukkit.getScheduler().runTaskTimer(PooleafAbilityPlugin.instance, {
            val abilityPlayer = abilityPlayer ?: return@runTaskTimer
            val player = abilityPlayer.player ?: return@runTaskTimer
            if (isRewinding || !abilityPlayer.isPlaying()) return@runTaskTimer

            records.addLast(Record(player.location.clone(), player.health))
            while (records.size > RECORD_LIMIT) {
                records.removeFirst()
            }
        }, 0L, 1L)
    }

    private fun stopRecording() {
        recordTask?.cancel()
        recordTask = null
    }

    // ----- 그림자 NPC -----

    private fun spawnShadow() {
        if (!isCitizensEnabled()) return
        val abilityPlayer = abilityPlayer ?: return
        val player = abilityPlayer.player ?: return
        if (!abilityPlayer.isPlaying()) return
        if (shadow != null) return

        val start = (records.peekFirst()?.location ?: player.location).clone()

        // NPC 이름을 시전자 닉네임으로 주면 해당 닉네임 스킨이 적용된다(본인 스킨).
        val npc = CitizensAPI.getNPCRegistry().createNPC(org.bukkit.entity.EntityType.PLAYER, player.name)
        npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false) // 머리 위 이름 숨김
        npc.spawn(start)
        shadow = npc

        startFollow()
    }

    // 매 틱 그림자를 6초 전(가장 오래된 기록) 위치로 옮겨 과거의 이동을 재생한다.
    private fun startFollow() {
        stopFollow()
        followTask = Bukkit.getScheduler().runTaskTimer(PooleafAbilityPlugin.instance, {
            val npc = shadow ?: return@runTaskTimer
            if (isRewinding || !npc.isSpawned) return@runTaskTimer
            val past = records.peekFirst() ?: return@runTaskTimer
            npc.teleport(past.location.clone(), PlayerTeleportEvent.TeleportCause.PLUGIN)
        }, 0L, 1L)
    }

    private fun stopFollow() {
        followTask?.cancel()
        followTask = null
    }

    private fun removeShadow() {
        stopFollow()
        shadow?.let { if (it.isSpawned) it.despawn(); it.destroy() }
        shadow = null
    }

    private fun isCitizensEnabled(): Boolean =
        Bukkit.getPluginManager().getPlugin("Citizens")?.isEnabled == true

    // ----- 역행 -----

    private fun startRewind() {
        val player = abilityPlayer?.player ?: return

        // 가장 오래된 기록(=6초 전, 그림자 위치)이 목적지.
        val past = records.peekFirst() ?: return
        val from = player.location.clone()
        val to = past.location.clone().apply {
            yaw = from.yaw
            pitch = from.pitch
        }
        val targetHealth = min(past.health, player.maxHealth)

        isRewinding = true
        stopFollow()
        records.clear()

        player.world.playSound(from, XSound.ENTITY_ENDERMAN_TELEPORT.parseSound(), 0.9F, 1.5F)

        var tick = 0
        rewindTask = Bukkit.getScheduler().runTaskTimer(PooleafAbilityPlugin.instance, {
            val current = abilityPlayer?.player
            if (current == null || !current.isOnline || tick >= REWIND_TICKS) {
                finishRewind(to, targetHealth)
                return@runTaskTimer
            }

            tick++
            val progress = tick.toDouble() / REWIND_TICKS
            val step = from.clone().add(
                Vector(to.x - from.x, to.y - from.y, to.z - from.z).multiply(progress)
            )
            current.teleport(step)
            Particle.PORTAL.spawn(step.clone().add(0.0, 1.0, 0.0), 0.3F, 12)
            spawnDamageRing(to)
        }, 0L, 1L)
    }

    private fun finishRewind(destination: Location, targetHealth: Double) {
        stopRewind()
        isRewinding = false

        val player = abilityPlayer?.player
        if (player != null && player.isOnline) {
            player.teleport(destination)
            player.health = targetHealth
            player.world.playSound(destination, XSound.ENTITY_ENDERMAN_TELEPORT.parseSound(), 0.9F, 0.8F)
        }

        explode(destination)
        removeShadow() // 능력 사용 후 그림자 소멸. 쿨타임 끝나면 재생성.
    }

    private fun explode(center: Location) {
        center.world.playSound(center, XSound.ENTITY_GENERIC_EXPLODE.parseSound(), 0.9F, 1.1F)
        Particle.PORTAL.spawn(center.clone().add(0.0, 1.0, 0.0), 0.8F, 60)
        Particle.SPELL_INSTANT.spawn(center.clone().add(0.0, 1.0, 0.0), 0.5F, 40)
        spawnDamageRing(center)

        val abilityPlayer = abilityPlayer ?: return
        AbilityApi.unsafe.playerManager.getOnlinePlayingPlayers()
            .filter { it != abilityPlayer }
            .filter { abilityPlayer.team == null || abilityPlayer.team != it.team }
            .filter { it.player.world == center.world }
            .filter { it.player.location.distance(center) <= DAMAGE_RANGE }
            .forEach {
                it.player.damageBypassAntiCheat(DAMAGE, abilityPlayer.player)
            }
    }

    private fun spawnDamageRing(center: Location) {
        for (i in 0 until 24) {
            val angle = 2 * PI * i / 24
            val location = center.clone().add(cos(angle) * DAMAGE_RANGE, 0.2, sin(angle) * DAMAGE_RANGE)
            Particle.SPELL_INSTANT.spawn(location, 0.0F, 1)
        }
    }

    private fun stopRewind() {
        rewindTask?.cancel()
        rewindTask = null
    }

    private data class Record(
        val location: Location,
        val health: Double,
    )

    companion object {
        private const val RECORD_LIMIT = 20 * 6   // 6초 (1틱 = 1기록)
        private const val REWIND_TICKS = 8         // 0.4초 만에 도착
        private const val DAMAGE = 10.0
        private const val DAMAGE_RANGE = 4.0
    }

}
