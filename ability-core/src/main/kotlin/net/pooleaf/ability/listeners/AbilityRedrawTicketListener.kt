package net.pooleaf.ability.listeners

import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.items.AbilityRedrawTicket
import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class AbilityRedrawTicketListener: Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return

        val item = event.item ?: return
        if (item.type == Material.AIR) return

        val ticket = AbilityRedrawTicket.getByItem(item) ?: return
        event.isCancelled = true

        val player = event.player
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isEnded) {
            player.sendWarning("게임 중에만 사용할 수 있습니다.")
            return
        }

        val currentPhase = AbilityApi.game.phasePipeline.currentPhase
        if (currentPhase is AbilityDrawPhase) {
            player.sendWarning("능력 추첨 중에는 사용할 수 없습니다.")
            return
        }

        val abilityPlayer = AbilityApi.unsafe.playerManager.get(player.uniqueId)
        if (abilityPlayer == null || !abilityPlayer.isJoined) {
            player.sendWarning("게임에 참여한 플레이어만 사용할 수 있습니다.")
            return
        }
        if (abilityPlayer.ability == null) {
            player.sendWarning("재추첨할 능력이 없습니다.")
            return
        }
        if (abilityPlayer.abilityDrawJob?.isActive == true) {
            player.sendWarning("이미 능력을 추첨 중입니다.")
            return
        }

        var abilities = AbilityApi.unsafe.abilityManager.getDefaultDrawAbilities()
            .filter { ticket.abilityRanks.contains(it.rank) }

        if (!AbilityApi.abilityGameConfig.allowAbilityDuplicate) {
            val assignedAbilityFullNames = AbilityApi.unsafe.playerManager.getJoinedPlayers()
                .filter { it.uuid != abilityPlayer.uuid }
                .mapNotNull { it.ability?.fullName }
            val tempAssignedAbilityFullNames = AbilityApi.unsafe.playerManager.getJoinedPlayers()
                .filter { it.uuid != abilityPlayer.uuid }
                .mapNotNull { it.tempAbility?.fullName }

            abilities = abilities
                .filter { !assignedAbilityFullNames.contains(it.fullName) }
                .filter { !tempAssignedAbilityFullNames.contains(it.fullName) }
        }

        if (abilities.isEmpty()) {
            player.sendWarning("재추첨 가능한 능력이 부족하여 아이템을 사용하지 않았습니다.")
            return
        }

        consumeItemInHand(player)

        abilityPlayer.abilityDrawJob = BukkitAsyncScope.launch {
            try {
                abilityPlayer.resignAbility()
                AbilityApi.unsafe.abilityService.drawAbility(
                    abilityPlayer = abilityPlayer,
                    abilities = abilities,
                    temp = false,
                    allowDuplicate = true
                )
            } finally {
                abilityPlayer.abilityDrawJob = null
            }
        }
    }

    private fun consumeItemInHand(player: Player) {
        val item = player.itemInHand
        if (item.amount <= 1) {
            player.itemInHand = ItemStack(Material.AIR)
        } else {
            item.amount = item.amount - 1
            player.itemInHand = item
        }

        player.updateInventory()
    }

}
