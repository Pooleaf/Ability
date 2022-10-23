package net.pooleaf.gamecore.phases

import com.cryptomorin.xseries.XSound
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameMapTeleportedEvent
import net.pooleaf.gamecore.phase.Phase
import org.bukkit.Bukkit

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
        if (GameCore.game.map == null) {
            Broadcaster.broadcastActionBar("§c맵이 존재하지 않아 텔레포트되지 않았습니다.")
        } else {
            val map = GameCore.game.map!!

            // 맵으로 텔레포트
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                map.teleportToRandomLocation(onlinePlayer)
            }
            Broadcaster.broadcastActionBar(map.displayName + " §e맵으로 이동되었습니다.")

            // 이벤트
            Bukkit.getPluginManager().callEvent(GameMapTeleportedEvent())
        }
    }

}