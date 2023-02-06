package net.pooleaf.gamecore.sql.dtos

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer

data class GameParticipantDto(
    val gameId: String,
    val playerUuid: String
) {
}

fun GamePlayer.toDto(): GameParticipantDto {
    val gameId = GameCore.game.gameId?.toString() ?: error("gameId cannot be null")

    return GameParticipantDto(gameId, this.uuid.toString())
}