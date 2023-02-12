package net.pooleaf.gamecore.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.common.util.toMillis
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player

class EndPhase(): Phase() {

    override suspend fun onStart() {
        // 우승 가능 시간이 안될경우 중단
        if (System.currentTimeMillis() - GameCore.game.startedAt!!.toMillis() < GameCore.gameConfig.winAllowSeconds) {
            GameCore.unsafe.gameManager.stopGame()
        }

        // 우승
        val winnerTeam = GameCore.unsafe.gameManager.onGameEnd()

        winnerTeam?.let { winnerTeam ->
            val winnerPlayers = winnerTeam?.players
            val winnerPlayerNames = winnerPlayers.joinToString { it.displayName }

            // 우승 타이틀
            Broadcaster.broadcastTitle("§e우승", "§f${winnerPlayerNames}", 10 * 20)

            // 사운드
            Broadcaster.broadcastSound(XSound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.5F)

            // 우승자 주변에 폭죽 날리기
            winnerPlayers.forEach { gamePlayer ->
                BukkitSyncScope.launch {
                    gamePlayer.player?.let { player ->
                        for (i in 1..5) {
                            if (player.isOnline) {
                                shootRandomFirework(player)
                            }
                            delay(200L)
                        }
                    }
                }
            }

            // 다시 시작 액션바
            for (count in 15 downTo 1) {
                when (count) {
                    in 4..10 -> {
                        Broadcaster.broadcastActionBar("§e${count}§c초 후 게임이 다시 시작됩니다.")
                    }
                    in 1..3 -> {
                        Broadcaster.broadcastActionBar("§e${count}§c초 후 게임이 다시 시작됩니다.")
                        Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3F, 0.7F)
                    }
                }

                delay(1000L)
            }
        }
    }

    override fun onEnd() {
        Broadcaster.removeActionBar()

        // 게임 리셋
        BukkitAsyncScope.launch {
            GameCore.unsafe.gameManager.resetGame()
        }
    }

    private fun shootRandomFirework(player: Player) {
        val location: Location = player.location
        location.add(Math.random() * 10 - 5, 0.0, Math.random() * 10 - 5)

        val firework = location.world.spawnEntity(location, EntityType.FIREWORK) as Firework

        val fireworkMeta = firework.fireworkMeta
        fireworkMeta.power = 1
        fireworkMeta.addEffect(
            FireworkEffect.builder()
                .withColor(getRandomColor())
                .build()
        )

        firework.fireworkMeta = fireworkMeta
    }

    private fun getRandomColor(): Color? {
        return getColor((Math.random() * 17 + 1).toInt())
    }

    private fun getColor(i: Int): Color? {
        when (i) {
            1 -> return Color.WHITE
            2 -> return Color.SILVER
            3 -> return Color.GRAY
            4 -> return Color.BLACK
            5 -> return Color.RED
            6 -> return Color.MAROON
            7 -> return Color.YELLOW
            8 -> return Color.OLIVE
            9 -> return Color.LIME
            10 -> return Color.GREEN
            11 -> return Color.AQUA
            12 -> return Color.TEAL
            13 -> return Color.BLUE
            14 -> return Color.NAVY
            15 -> return Color.FUCHSIA
            16 -> return Color.PURPLE
            17 -> return Color.ORANGE
        }
        return null
    }

}