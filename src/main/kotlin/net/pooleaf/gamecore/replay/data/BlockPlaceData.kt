package net.pooleaf.gamecore.replay.data

import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

/**
 * 블럭 설치 사운드 재생용
 * 블럭 변경은 [BlockChangeData]에서 담당
 */
class BlockPlaceData : RecordData {

    override val type: String = "blockPlace"

    lateinit var worldName: String
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0
    var blockTypeId: Int = 0


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer

        val location = Location(Bukkit.getWorld(worldName), x, y, z)

        val nmsBlock = BukkitReflectionUtil.getNmsBlock(blockTypeId)
        val breakSound = BukkitReflectionUtil.getBlockPlaceSound(nmsBlock)
        val volume = (BukkitReflectionUtil.getBlockSoundVolume(nmsBlock) + 1.0F) / 2.0F
        val pitch = BukkitReflectionUtil.getBlockSoundPitch(nmsBlock) * 0.8F

        viewer.playSound(location, breakSound, volume, pitch)
    }

}

class BlockPlaceDataListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val block = event.block
        val location = block.location

        val record = GameCore.unsafe.recordManager.record!!
        val recordData = BlockPlaceData().apply {
            worldName = location.world.name
            x = location.x
            y = location.y
            z = location.z
            blockTypeId = block.typeId
        }
        record.addRecordData(recordData)
    }

}