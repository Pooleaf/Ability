package net.pooleaf.gamecore.replay.record

import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.modules.support.common.util.GsonUtil
import net.pooleaf.gamecore.GameCore
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class RecordService {

    val recordFolder = File(GameCore.gamePlugin.dataFolder, "record")


    fun saveRecordToFile(record: Record) {
        val json = GsonUtil.getGson().toJson(record)

        recordFolder.mkdirs()

        val file = File(recordFolder, "${record.replay.uuid}.json")
        Files.write(file.toPath(), json.toByteArray(), StandardOpenOption.CREATE_NEW)

        Logger.log("${record.replay.uuid} 녹화를 저장했습니다.")
    }

}