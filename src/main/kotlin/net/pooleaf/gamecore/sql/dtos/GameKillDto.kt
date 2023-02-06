package net.pooleaf.gamecore.sql.dtos

import java.time.LocalDateTime

data class GameKillDto(
    val gameId: String,
    val killerPlayerUuid: String,
    val deadPlayerUuid: String,
    val killedAt: LocalDateTime
)
