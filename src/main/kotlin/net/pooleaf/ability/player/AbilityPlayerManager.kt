package net.pooleaf.ability.player

import net.pooleaf.gamecore.player.GamePlayerManager
import java.util.*

class AbilityPlayerManager: GamePlayerManager<AbilityPlayer>() {

    override fun create(uuid: UUID): AbilityPlayer {
        return AbilityPlayer(uuid)
    }

}