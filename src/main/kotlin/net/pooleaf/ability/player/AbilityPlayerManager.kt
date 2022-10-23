package net.pooleaf.ability.player

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayerManager
import net.pooleaf.gamecore.team.Team
import java.util.*

class AbilityPlayerManager: GamePlayerManager<AbilityPlayer>() {

    override fun create(uuid: UUID): AbilityPlayer {
        if (exists(uuid)) {
            return get(uuid)
        }

        val abilityPlayer = AbilityPlayer(uuid, Team())
        abilityPlayer.team.addPlayer(abilityPlayer)
        set(uuid, abilityPlayer)

        GameCore.teamManager.add(abilityPlayer.team)

        return abilityPlayer
    }

}