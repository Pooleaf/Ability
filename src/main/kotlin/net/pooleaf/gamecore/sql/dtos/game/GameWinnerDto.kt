package net.pooleaf.gamecore.sql.dtos.game

data class GameWinnerDto(
    var gameId: String? = null,
    var teamId: Int = -1,
    var winnerPlayerUuid: String? = null
) {
}