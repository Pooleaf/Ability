package net.pooleaf.gamecore.sql.dtos.replay

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.replay.Replay
import java.io.File
import java.time.LocalDateTime
import java.util.*

data class ReplayDto(
    val gameId: UUID,
    val createdAt: LocalDateTime,
    val endTick: Long
) {

    val replayFile: File
        get() = File(GameCore.unsafe.replayService.replayFolder, "${gameId}.json")

}

fun Replay.toDto(): ReplayDto {
    return ReplayDto(
        this.gameId,
        this.createdAt!!,
        this.endTick
    )
}