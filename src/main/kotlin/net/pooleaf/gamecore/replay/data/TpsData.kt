package net.pooleaf.gamecore.replay.data

import net.minecraft.server.v1_8_R3.MinecraftServer
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.replay.RecordTickEvent
import net.pooleaf.gamecore.replay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * 채팅 데이터
 * 관리자에게만 전송
 */
data class TpsData(
    var tps: Double = 0.0
) : RecordData {

    override val type: String = "tps"

}

class TpsDataRecordListener : Listener {

    @EventHandler
    fun onRecordTick(event: RecordTickEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        // 10초마다 기록
        if ((event.record.currentTick.toInt() % (10 * 20)) == 0) {
            val recordData = TpsData().apply {
                tps = MinecraftServer.getServer().tps1.average
            }
            GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
        }
    }

}

class TpsDataReplayHandler : RecordDataReplayHandler<TpsData> {

    override fun onPlay(recordData: TpsData, viewer: Player) {
        // TODO
    }

}