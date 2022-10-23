package net.pooleaf.gamecore.listeners

import com.cryptomorin.xseries.XSound
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.DefaultTitleBuilder
import net.pooleaf.gamecore.GameCore
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class AutoGameListener: Listener {

    /**
     * 게임 시작
     */
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        // 대기 중에 일정 인원이 되면 게임 시작
        if (GameCore.game.canStart()) {
            GameCore.game.start(null)
        }
    }

    /**
     * 게임 종료
     */
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        // Player가 오프라인 처리되고 계산되어야 하기 때문에 1 Tick 뒤에 실행함
        Bukkit.getScheduler().runTaskLater(GameCore.gamePlugin, {
            // 우승 가능한 시간이 지나야만 우승
            if (GameCore.game.canEnd()) {
                GameCore.game.end()
            }
            // 우승 불가능하고 한 팀만 남으면 게임 중단
            else if (GameCore.game.countingStarted && !GameCore.game.ended && GameCore.teamManager.getNotDefeatedOnlineTeams().size == 1) {
                GameCore.game.cancel()

                Broadcaster.broadcastTitle(
                    DefaultTitleBuilder()
                        .title("§c게임 중단")
                        .subtitle("§c게임 조건이 충족되지 않아 게임이 중단되었습니다.")
                        .stay(5 * 20)
                        .build()
                )
                Broadcaster.broadcastSound(XSound.ENTITY_ITEM_BREAK, 1F, 1F)
            }
            // 게임 중에 한 팀 빼고 퇴장하면 종료
        }, 1L)
    }

}