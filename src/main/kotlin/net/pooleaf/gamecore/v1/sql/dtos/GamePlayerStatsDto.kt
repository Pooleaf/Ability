package net.pooleaf.gamecore.v1.sql.dtos

data class GamePlayerStatsDto(
    val playerUuid: String,
    val gameTypeId: Int,
    val killCount: Int,
    val deathCount: Int,
    val assistCount: Int,
    val winCount: Int
)
