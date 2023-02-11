package net.pooleaf.gamecore.v1.sql.dtos

import net.pooleaf.gamecore.v1.player.GamePlayer

data class GameParticipantDto(
    val gameId: String,
    val playerUuid: String
) {
}

fun GamePlayer.toDto(): GameParticipantDto {
    val gameId = net.pooleaf.gamecore.v1.GameCore.game.gameId?.toString() ?: error("gameId cannot be null")

    return GameParticipantDto(gameId, this.uuid.toString())
}