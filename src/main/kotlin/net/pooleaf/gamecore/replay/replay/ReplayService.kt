package net.pooleaf.gamecore.replay.replay

import net.pooleaf.gamecore.GameCore
import org.bukkit.entity.Player
import java.util.UUID

class ReplayService {

    fun playReplay(viewer: Player, replayUuid: UUID) {
        if (isPlayingReplay(viewer)) error("Player already watching replay")

        val replay = GameCore.unsafe.replayManager.get(replayUuid) ?: error("Replay ${replayUuid} is not exist")

        val replayPlayer = ReplayPlayer(viewer, replay)
        replayPlayer.init()
        replayPlayer.play()
    }

    fun exitReplay(viewer: Player) {
        if (!isPlayingReplay(viewer)) error("Player not watching replay")

        val replayPlayer = GameCore.unsafe.replayPlayerManager.get(viewer.uniqueId)
        replayPlayer.exit()

        GameCore.unsafe.replayPlayerManager.remove(viewer.uniqueId)
    }

    fun isPlayingReplay(viewer: Player): Boolean {
        return GameCore.unsafe.replayPlayerManager.exists(viewer.uniqueId)
    }

}