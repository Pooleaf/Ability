package net.pooleaf.gamecore.sql.daos

import net.pooleaf.core.modules.sqllib.common.AbstractSqlManager
import net.pooleaf.core.modules.sqllib.common.SqlDao
import net.pooleaf.core.modules.sqllib.common.SqlTable
import net.pooleaf.gamecore.sql.dtos.*

class GameDao(sqlManager: AbstractSqlManager?) : SqlDao(sqlManager) {

    val gameTable = SqlTable(sqlManager, "games",
        "game_id VARCHAR(36) PRIMARY KEY",
        "game_type_id INT",
        "channel_name VARCHAR(50)",
        "started_at DATETIME",
        "ended_at DATETIME",
        "cancel_yn VARCHAR(1)"
        )

    val gameTypeTable = SqlTable(sqlManager, "game_types",
        "game_type_id INT PRIMARY KEY",
        "type_name VARCHAR(20)"
    )

    val gameParticipantTable = SqlTable(sqlManager, "game_participants",
        "game_id VARCHAR(36)",
        "player_uuid VARCHAR(36)",
        "PRIMARY KEY(game_id, player_uuid)"
    )

    val gameKillTable = SqlTable(sqlManager, "game_kills",
        "game_id VARCHAR(36)",
        "killer_player_uuid VARCHAR(36)",
        "dead_player_uuid VARCHAR(36)",
        "killed_at DATETIME"
    )

    val gameWinnerTable = SqlTable(sqlManager, "game_winners",
        "game_id VARCHAR(36)",
        "winner_player_uuid VARCHAR(36)",
        "PRIMARY KEY(game_id, winner_player_uuid)"
    )

    val gamePlayerStats = SqlTable(sqlManager, "game_player_stats",
        "player_uuid VARCHAR(36)",
        "game_type_id INT",
        "kill_count INT",
        "death_count INT",
        "assist_count INT",
        "win_count INT",
        "PRIMARY KEY(player_uuid, game_type_id)"
    )

    override fun onConnected() {
        gameTable.create()
        gameTypeTable.create()
        gameParticipantTable.create()
        gameKillTable.create()
        gameWinnerTable.create()
        gamePlayerStats.create()
    }

    /**
     * Game
     */

    fun insertGame(gameDto: GameDto) {
        gameTable.insertInto()
            .valuesByObject(gameDto)
            .onDuplicateKeyUpdate()
            .execute()
    }

    fun selectGame(gameId: String): GameDto {
        return gameTable.select("game_id = ?")
            .parameters(gameId)
            .execute(GameDto::class.java)
    }

    /**
     * GameType
     */

    fun insertGameType(gameTypeDto: GameTypeDto) {
        gameTypeTable.insertInto()
            .valuesByObject(gameTypeDto)
            .onDuplicateKeyUpdate()
            .execute()
    }

    fun selectGameTypes(): List<GameTypeDto> {
        return gameTypeTable.select()
            .executeList(GameTypeDto::class.java)
    }

    /**
     * GameParticipant
     */

    fun insertGameParticipants(gameParticipantDtos: List<GameParticipantDto>) {
        val context = gameParticipantTable.insertInto()
        gameParticipantDtos.forEach { context.valuesByObject(it) }
        context.execute()
    }

    fun selectGameParticipantsByGameId(gameId: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameParticipantTable.select("game_id = ?")
            .parameters(gameId)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    fun selectGameParticipantsByPlayerUuid(playerUuid: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameParticipantTable.select("player_uuid = ?")
            .parameters(playerUuid)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    /**
     * GameKill
     */

    fun insertGameKill(gameKillDto: GameKillDto) {
        gameKillTable.insertInto()
            .valuesByObject(gameKillDto)
            .execute()
    }

    fun selectGameKillsByGameId(gameId: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameKillTable.select("game_id = ?")
            .parameters(gameId)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    fun selectGameKillsByKillerPlayerUuid(killerPlayerUuid: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameKillTable.select("killer_player_uuid = ?")
            .parameters(killerPlayerUuid)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    fun selectGameKillsByDeadPlayerUuid(deadPlayerUuid: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameKillTable.select("dead_player_uuid = ?")
            .parameters(deadPlayerUuid)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    fun selectGameKillsByPlayerUuid(playerUuid: String, count: Int, offset: Int = 0): List<GameParticipantDto> {
        return gameKillTable.select("killer_player_uuid = ? OR dead_player_uuid = ?")
            .parameters(playerUuid)
            .parameters(playerUuid)
            .limit(offset, count)
            .executeList(GameParticipantDto::class.java)
    }

    /**
     * GameWinner
     */

    fun insertGameWinner(gameWinnerDtos: List<GameWinnerDto>) {
        val context = gameWinnerTable.insertInto()
        gameWinnerDtos.forEach { context.valuesByObject(it) }
        context.execute()
    }

    fun selectGameWinnersByGameId(gameId: String): List<GameWinnerDto> {
        return gameWinnerTable.select("game_id = ?")
            .parameters(gameId)
            .executeList(GameWinnerDto::class.java)
    }

    fun selectGameWinnersByPlayerUuid(playerUuid: String): List<GameWinnerDto> {
        return gameWinnerTable.select("winner_player_uuid = ?")
            .parameters(playerUuid)
            .executeList(GameWinnerDto::class.java)
    }

    /**
     * GamePlayerStats
     */

    fun insertGamePlayerStats(gamePlayerStatsDto: GamePlayerStatsDto) {
        gamePlayerStats.insertInto()
            .valuesByObject(gamePlayerStatsDto)
            .execute()
    }

    fun selectGamePlayerStats(count: Int, offset: Int = 0): List<GamePlayerStatsDto> {
        return gamePlayerStats.select()
            .limit(offset, count)
            .executeList(GamePlayerStatsDto::class.java)
    }

    fun selectGamePlayerStatsByPlayerUuid(playerUuid: String, count: Int, offset: Int = 0): List<GamePlayerStatsDto> {
        return gamePlayerStats.select("player_uuid = ?")
            .parameters(playerUuid)
            .limit(offset, count)
            .executeList(GamePlayerStatsDto::class.java)
    }

    fun selectGamePlayerStatsByGameTypeId(gameTypeId: Int, count: Int, offset: Int = 0): List<GamePlayerStatsDto> {
        return gamePlayerStats.select("game_type_id = ?")
            .parameters(gameTypeId)
            .limit(offset, count)
            .executeList(GamePlayerStatsDto::class.java)
    }

    fun selectGamePlayerStatsByPlayerUuidAndGameTypeId(playerUuid: String, gameTypeId: Int, count: Int, offset: Int = 0): List<GamePlayerStatsDto> {
        return gamePlayerStats.select("player_uuid = ? AND game_type_id = ?")
            .parameters(playerUuid)
            .parameters(gameTypeId)
            .limit(offset, count)
            .executeList(GamePlayerStatsDto::class.java)
    }

}