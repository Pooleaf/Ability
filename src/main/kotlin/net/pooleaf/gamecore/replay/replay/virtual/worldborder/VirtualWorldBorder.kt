package net.pooleaf.gamecore.replay.replay.virtual.worldborder

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.game.GameWorldBorderChangeData
import net.pooleaf.gamecore.replay.replay.virtual.VirtualHistory
import org.bukkit.entity.Player

class VirtualWorldBorder : VirtualHistory() {

    fun timeMachine(tick: Long, viewer: Player) {
        getLastData(GameWorldBorderChangeData::class.java, tick)?.let { data ->
            val playerHandler = GameCore.unsafe.recordDataManager.get(data.javaClass) ?: return
            playerHandler.onPlay(data, viewer)
        }
    }

}