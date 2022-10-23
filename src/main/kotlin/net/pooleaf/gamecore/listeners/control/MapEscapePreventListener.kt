package net.pooleaf.gamecore.listeners.control

import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.GameCore
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent

class MapEscapePreventListener: Listener {

    private fun isTeleportedToMap(): Boolean {
        return GameCore.game.mapTeleported;
    }

    private fun isInMap(location: Location): Boolean {
        return GameCore.game.map?.isInRadius(location) ?: true
    }


    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        Bukkit.getScheduler().runTaskLater(GameCore.gamePlugin, {
            if (isTeleportedToMap() && !isInMap(event.player.location)) {
                TeleportUtil.teleport(event.player, GameCore.game.map!!.getCenterLocation())
            }
        }, 20L)
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (isTeleportedToMap() && !isInMap(event.to)) {
            // 원래 위치가 맵 안이라면 원래 위치로
            if (isInMap(event.from)) {
                event.to = event.from
            }
            // 원래 위치가 맵 밖이라면 맵 중앙으로
            else {
                TeleportUtil.teleport(event.player, GameCore.game.map!!.getCenterLocation())
            }
        }
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        if (isTeleportedToMap() && !isInMap(event.to)) {
            // 원래 위치가 맵 안이라면 원래 위치로
            if (isInMap(event.from)) {
                event.to = event.from
            }
            // 원래 위치가 맵 밖이라면 맵 중앙으로
            else {
                TeleportUtil.teleport(event.player, GameCore.game.map!!.getCenterLocation())
            }
        }
    }

}