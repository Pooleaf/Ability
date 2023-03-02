package net.pooleaf.gamecore.replay.replay

import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.modules.support.common.util.GsonUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.sql.dtos.replay.ReplayDto
import net.pooleaf.gamecore.sql.dtos.replay.toDto
import org.bukkit.entity.Player
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*

class ReplayService {

    val replayFolder = File(GameCore.gamePlugin.dataFolder, "replay")

    val gson = GsonUtil.gsonBuilder
        .registerTypeHierarchyAdapter(RecordData::class.java, RecordDataDeserializer())
        .create()


    fun saveReplay(replay: Replay) {
        saveReplayToFile(replay)
        saveReplayToDatabase(replay)
    }

    fun saveReplayToFile(replay: Replay) {
        val json = gson.toJson(replay)

        replayFolder.mkdirs()

        val file = File(replayFolder, "${replay.gameId}.json")
        Files.write(file.toPath(), json.toByteArray(), StandardOpenOption.CREATE_NEW)

        Logger.log("${replay.gameId} 녹화 파일을 저장했습니다.")
    }

    fun saveReplayToDatabase(replay: Replay) {
        GameCore.unsafe.sqlManager.replayDao.insertReplay(replay.toDto())

        Logger.log("${replay.gameId} 녹화를 DB에 저장했습니다.")
    }

    fun playReplay(viewer: Player, replayUuid: UUID) {
        if (isPlayingReplay(viewer)) error("Player already watching replay")

        val replay = GameCore.unsafe.replayManager.get(replayUuid) ?: error("Replay ${replayUuid} is not exist")

        val replayPlayer = ReplayPlayer(viewer, replay)
        replayPlayer.init()
        replayPlayer.play()

        GameCore.unsafe.replayPlayerManager.set(viewer.uniqueId, replayPlayer)
    }

    fun exitReplay(viewer: Player) {
        if (!isPlayingReplay(viewer)) error("Player not watching replay")

        val replayPlayer = GameCore.unsafe.replayPlayerManager.get(viewer.uniqueId)
        replayPlayer.exit()

        GameCore.unsafe.replayPlayerManager.remove(viewer.uniqueId)
    }

    fun isPlayingReplay(viewer: Player): Boolean {
        return GameCore.unsafe.replayPlayerManager.exists(viewer.uniqueId)
    }

    fun selectReplayDtoNoCache(gameId: UUID, download: Boolean): ReplayDto? {
        return GameCore.unsafe.sqlManager.replayDao.selectReplay(gameId.toString(), download)
    }

    /**
     * DB에서 리플레이 목록을 불러와 반환합니다.
     * 파일을 다운로드 하지 않습니다.
     */
    fun selectReplayDtoListNoCache(gameId: List<UUID>, count: Int, offset: Int = 0): List<ReplayDto> {
        return GameCore.unsafe.sqlManager.replayDao.selectReplayList(gameId, count, offset)
    }

    fun loadReplayFromFile(gameId: UUID): Replay? {
        val file = File(replayFolder, "${gameId}.json")
        if (!file.exists()) return null

        val json = file.readLines().joinToString(" ")
        return gson.fromJson(json, Replay::class.java)
    }

    fun loadReplayFromDatabase(gameId: UUID): Replay? {
        val replayDto = selectReplayDtoNoCache(gameId, true)
        val replay = loadReplayFromFile(gameId)
        GameCore.unsafe.replayManager.set(gameId, replay)

        GameCore.unsafe.replayManager.setTimeToIdle(gameId, 5 * 60)

        return replay
    }

}