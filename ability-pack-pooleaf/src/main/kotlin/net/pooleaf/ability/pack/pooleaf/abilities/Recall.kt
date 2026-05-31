package net.pooleaf.ability.pack.pooleaf.abilities

import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import net.pooleaf.core.modules.support.bukkit.particle.Particle
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.util.ArrayDeque
import kotlin.math.min

class Recall : Ability(), Listener, CastByItemHandler, Cooldownable {

    private val records = ArrayDeque<RecallRecord>()
    private var recordTask: BukkitTask? = null
    private var replayTask: BukkitTask? = null
    private var isRecalling = false

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "시간 역행"
        rank = AbilityRank.SS
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 우클릭 시 6초 전의 체력과 위치로 돌아갑니다.",
            "역행 중에는 다른 플레이어에게 보이지 않습니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 60_000L)


    override fun onAssign() {
        startRecording()
    }

    override fun onResign() {
        stopRecording()
        stopReplay()
        showToEveryone()
    }

    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        if (clickType != CastByItemHandler.ClickType.RIGHT) return false
        if (isRecalling || records.isEmpty()) return false

        startRecall()
        return true
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = abilityPlayer?.player ?: return
        if (isRecalling) {
            event.player.hidePlayer(player)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = abilityPlayer?.player ?: return
        event.player.showPlayer(player)
    }

    private fun startRecording() {
        stopRecording()
        recordTask = Bukkit.getScheduler().runTaskTimer(PooleafAbilityPlugin.instance, {
            val abilityPlayer = abilityPlayer ?: return@runTaskTimer
            val player = abilityPlayer.player ?: return@runTaskTimer
            if (isRecalling || !abilityPlayer.isPlaying()) return@runTaskTimer

            records.addLast(RecallRecord(player.location.clone(), player.health))
            while (records.size > RECORD_LIMIT) {
                records.removeFirst()
            }
        }, 0L, 1L)
    }

    private fun stopRecording() {
        recordTask?.cancel()
        recordTask = null
    }

    private fun startRecall() {
        val player = abilityPlayer?.player ?: return
        val replayRecords = records.toList().asReversed()
        if (replayRecords.isEmpty()) return

        isRecalling = true
        hideFromEveryone()
        player.world.playSound(player.location, XSound.ENTITY_ENDERMAN_TELEPORT.parseSound(), 0.8F, 1.4F)
        spawnStartParticles(player.location)

        var index = 0
        replayTask = Bukkit.getScheduler().runTaskTimer(PooleafAbilityPlugin.instance, {
            val currentPlayer = abilityPlayer?.player
            if (currentPlayer == null || !currentPlayer.isOnline || index >= replayRecords.size) {
                finishRecall(replayRecords.last())
                return@runTaskTimer
            }

            val record = replayRecords[index++]
            currentPlayer.teleport(record.location)
            currentPlayer.health = min(record.health, currentPlayer.maxHealth)
            spawnRecallParticles(record.location)
        }, 0L, 1L)
    }

    private fun finishRecall(lastRecord: RecallRecord) {
        val player = abilityPlayer?.player

        stopReplay()
        isRecalling = false
        records.clear()
        showToEveryone()

        if (player != null && player.isOnline) {
            player.teleport(lastRecord.location)
            player.health = min(lastRecord.health, player.maxHealth)
            player.world.playSound(player.location, XSound.ENTITY_ENDERMAN_TELEPORT.parseSound(), 0.8F, 0.75F)
            Particle.PORTAL.spawn(player.location.clone().add(0.0, 1.0, 0.0), 0.6F, 50)
            Particle.SPELL_INSTANT.spawn(player.location.clone().add(0.0, 1.0, 0.0), 0.35F, 30)
        }
    }

    private fun stopReplay() {
        replayTask?.cancel()
        replayTask = null
    }

    private fun hideFromEveryone() {
        val player = abilityPlayer?.player ?: return
        Bukkit.getOnlinePlayers()
            .filter { it != player }
            .forEach { it.hidePlayer(player) }
    }

    private fun showToEveryone() {
        val player = abilityPlayer?.player ?: return
        Bukkit.getOnlinePlayers()
            .filter { it != player }
            .forEach { it.showPlayer(player) }
    }

    private fun spawnRecallParticles(location: Location) {
        Particle.PORTAL.spawn(location.clone().add(0.0, 1.0, 0.0), 0.2F, 4)
    }

    private fun spawnStartParticles(location: Location) {
        Particle.PORTAL.spawn(location.clone().add(0.0, 1.0, 0.0), 0.7F, 60)
        Particle.SPELL_INSTANT.spawn(location.clone().add(0.0, 1.0, 0.0), 0.45F, 24)
    }

    private data class RecallRecord(
        val location: Location,
        val health: Double,
    )

    companion object {
        private const val RECORD_LIMIT = 20 * 6
    }

}
