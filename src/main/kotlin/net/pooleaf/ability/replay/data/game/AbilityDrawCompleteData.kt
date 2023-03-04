package net.pooleaf.ability.replay.data.game

import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.event.game.AbilityDrawCompleteEvent
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamereplay.GameReplayApi
import net.pooleaf.gamereplay.data.RecordData
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * 능력 확정 완료 데이터
 */
class AbilityDrawCompleteData : RecordData {

    override val type: String = "abilityDrawComplete"

}

class AbilityDrawCompleteDataRecordListener : Listener {

    @EventHandler
    fun onAbilityDrawComplete(event: AbilityDrawCompleteEvent) {
        if (!GameReplayApi.unsafe.recordManager.isRecording()) return

        val recordData = AbilityDrawCompleteData()
        GameReplayApi.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}

class AbilityDrawCompleteDataReplayHandler : RecordDataReplayHandler<AbilityDrawCompleteData> {

    override fun onPlay(recordData: AbilityDrawCompleteData, viewer: Player) {
        Broadcaster.broadcast("§e모든 플레이어가 능력을 확정했습니다.")
        Broadcaster.broadcastSound(XSound.ENTITY_PLAYER_LEVELUP, 0.4F, 1.0F)
    }

}