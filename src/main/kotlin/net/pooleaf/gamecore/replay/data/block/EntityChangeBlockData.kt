package net.pooleaf.gamecore.replay.data.block

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import net.pooleaf.gamecore.replay.replay.virtual.block.VirtualBlock
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent

data class EntityChangeBlockData(
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
    var blockTypeId: Int = 0,
    var blockData: Byte = 0
) : RecordData {

    override val type: String = "entityChangeBlock"

}

class EntityChangeBlockDataRecordListener : Listener {

    @EventHandler
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val recordData = EntityChangeBlockData().apply {
            x = event.block.x
            y = event.block.y
            z = event.block.z
            blockTypeId = event.to.id
            blockData = event.data
        }

        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class EntityChangeBlockDataReplayHandler : RecordDataReplayHandler<EntityChangeBlockData> {

    override fun onPlay(recordData: EntityChangeBlockData, viewer: Player) {
        val replayPlayer = GameCore.unsafe.replayPlayerManager.get(viewer.uniqueId)

        val virtualBlock = replayPlayer.virtualBlockManager.getByXyz(recordData.x, recordData.y, recordData.z)!!
        virtualBlock.typeId = recordData.blockTypeId
        virtualBlock.typeData = recordData.blockData

        virtualBlock.showTo(viewer)
    }

}