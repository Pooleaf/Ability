package net.pooleaf.gamecore.phases

import com.cryptomorin.xseries.XSound
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase

open class MapTeleportCountPhase: Phase() {

    override fun onStart() {
        Broadcaster.broadcastActionBar("§e잠시 후 맵으로 이동됩니다.")
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
       GameCore.game.teleportToMap()
    }

}