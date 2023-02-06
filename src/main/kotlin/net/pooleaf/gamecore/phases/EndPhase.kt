package net.pooleaf.gamecore.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.commonscheduler.CommonSchedulerModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.plugin.CorePlugin
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.DefaultTitleBuilder
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player

open class EndPhase: Phase() {

    override fun onStart() {
        // 우승자 계산
        val winners = GameCore.game.winners!!

        // 게임 종료
        GameCore.game.end()

        // 우승 타이틀 띄우기
        val winnerNames: String = winners.joinToString(", ") { it.displayName }

        Broadcaster.broadcastTitle(
            DefaultTitleBuilder()
                .title("§e우승")
                .subtitle("§f$winnerNames")
                .stay(20 * 10)
                .build()
        )

        // 사운드
        Broadcaster.broadcastSound(XSound.ENTITY_PLAYER_LEVELUP, 1f, 0.5f)

        // 우승자 주변에 폭죽 터뜨리기
        winners.filter { it.isOnline }
            .forEach {
                CommonSchedulerModule.bukkit().scheduler.runSyncRepeat(GameCore.gamePlugin as CorePlugin, {
                    shootRandomFirework(it.player)
                }, 0, 4L, 5)
            }
    }

    override fun onRun() {
        when (val counter = 15 - count) {
            in 4..10 -> {
                Broadcaster.broadcastActionBar("§e${counter}§c초 후 게임이 다시 시작됩니다.")
            }
            in 1..3 -> {
                Broadcaster.broadcastActionBar("§e${counter}§c초 후 게임이 다시 시작됩니다.")
                Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3f, 0.7f)
            }
            0 -> {
                end()
            }
        }
    }

    override fun onEnd() {
        Broadcaster.removeActionBar()

        BukkitAsyncScope.launch {
            GameCore.game.endReset()
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