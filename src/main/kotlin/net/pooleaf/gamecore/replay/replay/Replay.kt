package net.pooleaf.gamecore.replay.replay

import net.pooleaf.gamecore.replay.data.RecordData
import java.time.LocalDateTime
import java.util.*

class Replay(
    val uuid: UUID,
    var recordedPlayers: List<UUID>
) {

    var startedAt: LocalDateTime? = null
    var endedAt: LocalDateTime? = null

    var endTick: Long = 0L

    var recordDatas: HashMap<Long, LinkedList<RecordData>> = hashMapOf()

}