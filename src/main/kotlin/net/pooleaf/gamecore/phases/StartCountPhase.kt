package net.pooleaf.gamecore.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.gui.GuiModule
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameStartedEvent
import net.pooleaf.gamecore.phase.Phase
import org.bukkit.Bukkit

open class StartCountPhase(val teleportToMap: Boolean): Phase() {

    override fun onStart() {
        GameCore.game.countingStarted = true

        Broadcaster.broadcastActionBar("§e잠시 후 게임이 시작됩니다.")
        Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3f, 0.7f)
    }

    override fun onRun() {
        when (val counter = 10 - count) {
            in 4..5 -> {
                Broadcaster.broadcastTitle("§e${counter}")
                Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3f, 0.7f)
            }
            in 1..3 -> {
                Broadcaster.broadcastTitle("§c${counter}")
                Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3f, 0.7f)
            }
            0 -> {
                end()
            }
        }
    }

    override fun onEnd() {
        // 팀 매칭
        GameCore.teamManager.matchingAndCreateTeams()

        // 게임 시작
        GameCore.game.gameStarted = true
        Bukkit.getPluginManager().callEvent(GameStartedEvent())

        GameCore.playerManager.getObservers().forEach { GameCore.quickBarManager.observerQuickBar.setTo(it.player) }
        GameCore.playerManager.getOnlineJoinedPlayers().forEach { GuiModule.getQuickBarManager().removeTo(it.player) }

        // 맵으로 텔레포트
        if (teleportToMap) {
            BukkitSyncScope.launch {
                GameCore.game.teleportToMap()
            }
        }
    }

}