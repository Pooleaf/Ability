package net.pooleaf.ability.player

import net.pooleaf.gamecore.player.GamePlayerFactory
import java.util.*

class AbilityPlayerFactory: GamePlayerFactory<AbilityPlayer> {

    override fun createGamePlayer(uuid: UUID): AbilityPlayer {
        return AbilityPlayer(uuid)
    }

}