package net.pooleaf.gamecore.v1.phases

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.gamecore.v1.Broadcaster
import net.pooleaf.gamecore.v1.GameCore
import net.pooleaf.gamecore.v1.phase.Phase

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
       BukkitSyncScope.launch {
           GameCore.game.teleportToMap()
       }
    }

}