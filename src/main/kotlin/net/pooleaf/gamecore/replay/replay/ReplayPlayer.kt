package net.pooleaf.gamecore.replay.replay

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.trait.trait.Owner
import net.citizensnpcs.trait.Gravity
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.replay.ReplayExitEvent
import net.pooleaf.gamecore.events.replay.ReplayPlayEvent
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.random.Random

/**
 * 리플레이 재생기
 */
class ReplayPlayer(
    val viewer: Player,
    val replay: Replay
) {

    var currentTick: Float = 0.0F
    var playSpeed: Float = 1.0F

    var replayTask: BukkitTask? = null

    // 플레이어 UUID, 리플레이 NPC
    val npcs = HashMap<UUID, ReplayNpc>()

    // 다른 Entity와 Id가 겹치지 않도록
    val entityIdOffset = 10000 + Random.nextInt(10000)

    // 임시 데이터 (뒤로감기를 위함)
    val tempDatas = hashMapOf<String, Any>()


    fun isRunning(): Boolean {
        return replayTask != null
    }

    fun init() {
        replay.recordedPlayers.forEach { uuid ->
            val commonPlayer = CommonSenderModule.getPlayer(uuid)

            val npcName = commonPlayer?.name ?: "Unknown"
            val citizensNpc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, npcName)
            citizensNpc.isProtected = true
            citizensNpc.getOrAddTrait(Owner::class.java).setOwner(viewer)
            citizensNpc.getOrAddTrait(Gravity::class.java).toggle()
            citizensNpc.spawn(viewer.location)

            val replayNpc = ReplayNpc(citizensNpc)
            npcs.put(uuid, replayNpc)
        }
    }

    fun play() {
        if (isRunning()) error("Replay already running")

        replayTask = object : BukkitRunnable() {
            override fun run() {
                val tickRecordDatas = replay.recordDatas.get(currentTick.toLong())
                tickRecordDatas?.forEach { it.onPlay(this@ReplayPlayer) }

                currentTick += playSpeed

                if (currentTick >= replay.endTick) {
                    exit()
                    pause()
                }
            }
        }.runTaskTimer(GameCore.gamePlugin, 0L, 1L)

        // 이벤트
        Bukkit.getPluginManager().callEvent(ReplayPlayEvent(this))
    }

    fun pause() {
        if (!isRunning()) error("Replay not running")

        replayTask?.cancel()
        replayTask = null
    }

    fun exit() {
        // NPC 제거
        npcs.values.forEach { it.citizensNpc.destroy() }

        // 뷰어 텔레포트
        GameCore.spawnConfig.spawnLocation?.let { spawnLocation -> TeleportUtil.teleport(viewer, spawnLocation) }

        // 이벤트
        Bukkit.getPluginManager().callEvent(ReplayExitEvent(this))
    }

}