package net.pooleaf.gamecore.listeners

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameEndEvent
import net.pooleaf.gamecore.events.player.GamePlayerDeathEvent
import net.pooleaf.gamecore.sql.dtos.game.GameKillDto
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.LocalDateTime

class PlayerStatsListener : Listener {

    @EventHandler
    fun onPlayerDeath(event: GamePlayerDeathEvent) {
        if (!GameCore.game.isGameStarted) return

        val deadPlayer = event.deadGamePlayer
        val killerPlayer = event.killerGamePlayer

        val gameTypeId = GameCore.game.gameTypeId

        BukkitAsyncScope.launch {
            // 킬 정보 저장
            val gameKillDto = GameKillDto(
                GameCore.game.gameId.toString(),
                killerPlayer?.uuid.toString(),
                deadPlayer.uuid.toString(),
                LocalDateTime.now()
            )

            GameCore.unsafe.sqlManager.gameDao.insertGameKill(gameKillDto)

            // 전적 저장
            if (killerPlayer != null) {
                GameCore.unsafe.sqlManager.gameDao.addGamePlayerStatsKillCount(killerPlayer.uuid, gameTypeId, 1)
            }
            GameCore.unsafe.sqlManager.gameDao.addGamePlayerStatsDeathCount(deadPlayer.uuid, gameTypeId, 1)

            // TODO 어시스트 저장
        }
    }

    @EventHandler
    fun onPlayerWin(event: GameEndEvent) {
        val winnerTeam = event.winnerTeam
        if (winnerTeam == null) return

        val gameTypeId = GameCore.game.gameTypeId

        BukkitAsyncScope.launch {
            winnerTeam.players.forEach { gamePlayer ->
                GameCore.unsafe.sqlManager.gameDao.addGamePlayerStatsWinCount(gamePlayer.uuid, gameTypeId, 1)
            }
        }
    }

}