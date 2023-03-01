package net.pooleaf.gamecore.replay.record

import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.replay.RecordStartEvent
import net.pooleaf.gamecore.events.replay.RecordStopEvent
import net.pooleaf.gamecore.events.replay.RecordTickEvent
import net.pooleaf.gamecore.replay.data.block.BlockChangeDataListener
import net.pooleaf.gamecore.replay.data.block.MultiBlockChangeDataRecordListener
import net.pooleaf.gamecore.replay.data.entity.*
import net.pooleaf.gamecore.replay.data.player.PlayerMetaDataDataRecordListener
import net.pooleaf.gamecore.replay.data.player.PlayerMoveData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.time.LocalDateTime
import java.util.*

class RecordManager {

    var record: Record? = null

    var recordTickCalculateTask: BukkitTask? = null


    fun registerRecordListeners() {
//        ProtocolLibrary.getProtocolManager().addPacketListener(TestPacketListener()) // TODO remove

        // Block
        ProtocolLibrary.getProtocolManager().addPacketListener(BlockChangeDataListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(MultiBlockChangeDataRecordListener())

        // Entity
        ProtocolLibrary.getProtocolManager().addPacketListener(CollectDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(EntityDestroyDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(EntityVelocityDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(ItemMetaDataDataRecordListener())
        ProtocolLibrary.getProtocolManager().addPacketListener(SpawnEntityDataRecordListener())

        // Player
        ProtocolLibrary.getProtocolManager().addPacketListener(PlayerMetaDataDataRecordListener())
    }

    /**
     * 녹화 중 여부를 반환합니다.
     */
    fun isRecording(): Boolean {
        return record?.let { it.isRecording } == true
    }

    /**
     * 녹화를 시작합니다.
     */
    fun startRecord(gameUuid: UUID, recordTargetPlayers: List<UUID>) {
        if (isRecording()) error("Recording already started")

        record = Record(gameUuid, recordTargetPlayers)
        record?.let { record ->
            record.isRecording = true
            record.replay.startedAt = LocalDateTime.now()

            // 플레이어 초기 데이터
            recordTargetPlayers.forEach { uuid ->
                val player = Bukkit.getPlayer(uuid)
                if (player == null) return@forEach

                val location = player.location

                val playerMoveData = PlayerMoveData().apply {
                    playerUuid = uuid
                    worldName = location.world.name
                    x = location.x
                    y = location.y
                    z = location.z
                    yaw = location.yaw
                    pitch = location.pitch
                }
                record.addRecordData(playerMoveData)
            }

            recordTickCalculateTask = Bukkit.getScheduler().runTaskTimer(GameCore.gamePlugin, {
                if (!isRecording()) return@runTaskTimer

                // 이벤트
                Bukkit.getPluginManager().callEvent(RecordTickEvent(record))

                // 틱 계산
                record.currentTick++
            }, 0L, 1L)

            // 이벤트
            Bukkit.getPluginManager().callEvent(RecordStartEvent(record))
        }
    }

    /**
     * 녹화를 중지합니다.
     */
    fun endRecord() {
        if (!isRecording()) error("Recording not started")

        record?.let { record ->
            record.isRecording = false
            record.replay.endTick = record.currentTick.toLong()
            record.replay.endedAt = LocalDateTime.now()

            recordTickCalculateTask?.cancel()

            // 저장
            // TODO 저장
//            GameCore.unsafe.recordService.saveRecordToFile(record)
            GameCore.unsafe.replayManager.set(record.replay.uuid, record.replay)

            // 이벤트
            Bukkit.getPluginManager().callEvent(RecordStopEvent(record))
        }
    }

    /**
     * 녹화 대상 플레이어인지를 반환합니다.
     */
    fun isRecordingTargetPlayer(player: Player): Boolean {
        return record?.let { it.recordTargetPlayers.contains(player.uniqueId) } == true
    }

}