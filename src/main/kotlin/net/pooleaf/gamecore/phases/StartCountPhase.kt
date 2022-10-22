package net.pooleaf.gamecore.phases

import com.cryptomorin.xseries.XSound
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase

class StartCountPhase: Phase() {

    override fun onStart() {
        GameCore.game.countingStarted = true

        Broadcaster.broadcastActionBar("§e잠시 후 게임이 시작됩니다.")
        Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3f, 0.7f)
    }

    override fun onRun() {
        when (val counter = 10 - count) {
            5,
            4 -> {
                Broadcaster.broadcastTitle("§e${counter}")
                Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3f, 0.7f)
            }
            3,
            2,
            1 -> {
                Broadcaster.broadcastTitle("§c${counter}")
                Broadcaster.broadcastSound(XSound.UI_BUTTON_CLICK, 0.3f, 0.7f)
            }
            0 -> {
                end()
            }
        }
    }

}