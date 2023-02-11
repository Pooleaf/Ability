package net.pooleaf.gamecore.v1.sql.dtos

import java.time.LocalDateTime

data class GameKillDto(
    val gameId: String,
    val killerPlayerUuid: String,
    val deadPlayerUuid: String,
    val killedAt: LocalDateTime
)
