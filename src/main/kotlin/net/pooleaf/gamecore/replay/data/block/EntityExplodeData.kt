package net.pooleaf.gamecore.replay.data.block

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import net.pooleaf.gamecore.replay.replay.virtual.block.VirtualBlock
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent

data class EntityExplodeData(
    var yield: Float = 0.0F,
    var blockInfos: List<BlockExplodeInfo> = arrayListOf()
) : RecordData {

    override val type: String = "entityExplode"

}

data class BlockExplodeInfo(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0
) {
}

class EntityExplodeDataRecordListener : Listener {

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val blockInfos = event.blockList().map { block ->
            BlockExplodeInfo().apply {
                x = block.x
                y = block.y
                z = block.z
            }
        }

        val recordData = EntityExplodeData().apply {
            yield = event.yield
            this.blockInfos = blockInfos
        }

        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class EntityExplodeDataReplayHandler : RecordDataReplayHandler<EntityExplodeData> {

    override fun onPlay(recordData: EntityExplodeData, viewer: Player) {
        val replayPlayer = GameCore.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val virtualBlocks = arrayListOf<VirtualBlock>()
        recordData.blockInfos.forEach { blockInfo ->
            val virtualBlock = replayPlayer.virtualBlockManager.getByXyz(blockInfo.x, blockInfo.y, blockInfo.z)!!
            virtualBlock.typeId = 0
            virtualBlock.typeData = 0

            virtualBlocks.add(virtualBlock)
        }

        replayPlayer.virtualBlockManager.showToBulk(virtualBlocks, viewer)
    }

}