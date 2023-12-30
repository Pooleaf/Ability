package net.pooleaf.ability.listeners

import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.gamecore.events.player.GamePlayerInitEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import net.pooleaf.permission.common.PermissionApi

class AbilityPlayerListener: Listener {

    @EventHandler
    fun onPlayerInit(event: GamePlayerInitEvent) {
        val abilityPlayer = event.gamePlayer as AbilityPlayer

        abilityPlayer.resignAbility()
        abilityPlayer.tempAbility = null
        abilityPlayer.isAbilityAssignReserved = false

        abilityPlayer.redrawCount = 0
        abilityPlayer.maxRedrawCount = 1

        abilityPlayer.abilityDrawComplete = false

        abilityPlayer.abilityDrawJob?.cancel()
        abilityPlayer.abilityDrawJob = null

        // 능력 재추첨 횟수 불러오기
        val permissionPlayer = PermissionApi.getPlayer(abilityPlayer.uuid) ?: return

        val nodes = permissionPlayer.nodes
        if (permissionPlayer.hasGroup()) {
            nodes.addAll(permissionPlayer.group.allNodes)
        }

        val redrawCountNode = nodes.firstOrNull { it.startsWith("ability.redrawcount.", true) }
        if (redrawCountNode != null) {
            val redrawCount = redrawCountNode.lowercase().substringAfter("ability.redrawcount.").toIntOrNull()
            if (redrawCount != null) {
                abilityPlayer.maxRedrawCount = redrawCount
            }
        }
    }

}