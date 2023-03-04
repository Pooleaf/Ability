package net.pooleaf.gamecore.replay.data.player

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import java.util.*

/**
 * 플레이어 가리기 데이터
 */
data class PlayerHideData(
    var playerUuid: UUID? = null,
) : RecordData {

    override val type: String = "playerHide"

}

class PlayerHideDataReplayHandler : RecordDataReplayHandler<PlayerHideData> {

    override fun onPlay(recordData: PlayerHideData, viewer: Player) {
        // NPC 가리기
        val replayPlayer = GameCore.unsafe.replayPlayerManager.get(viewer.uniqueId)
        val citizensNpc = replayPlayer.virtualPlayerManager.get(recordData.playerUuid).citizensNpc
        viewer.hidePlayer(citizensNpc.entity as Player?)
    }

}