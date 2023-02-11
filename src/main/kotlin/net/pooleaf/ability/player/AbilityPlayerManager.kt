package net.pooleaf.ability.player

import net.pooleaf.gamecore.v1.player.GamePlayerManager
import java.util.*

class AbilityPlayerManager: GamePlayerManager<AbilityPlayer>() {

    override fun create(uuid: UUID): AbilityPlayer {
        return AbilityPlayer(uuid)
    }

}