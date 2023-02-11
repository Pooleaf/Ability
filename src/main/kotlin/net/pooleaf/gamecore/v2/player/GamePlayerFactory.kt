package net.pooleaf.gamecore.v2.player

import java.util.UUID

interface GamePlayerFactory<T: GamePlayer> {

    fun createGamePlayer(uuid: UUID): T

}