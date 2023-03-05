package net.pooleaf.ability.listeners

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Durationable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.core.modules.support.common.logger.Logger
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action.*
import org.bukkit.event.player.PlayerInteractEvent

class AbilityCastListener: Listener {

    /**
     * 아이템으로 캐스팅하는 능력 처리
     */
    @EventHandler
    fun handleCastByItem(event: PlayerInteractEvent) {
        // 게임 중 체크
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isEnded || AbilityApi.game.isGodMode) return

        // 클릭 타입 계산
        val clickType = when (event.action) {
            LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> CastByItemHandler.ClickType.LEFT
            RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> CastByItemHandler.ClickType.RIGHT
            else -> return
        }

        val abilityPlayer = AbilityApi.unsafe.playerManager.get(event.player.uniqueId)
        val ability = abilityPlayer?.ability ?: return

        // 아이템으로 캐스팅하는 능력일 경우
        if (ability is CastByItemHandler) {
            // 쿨타임, 지속시간 체크
            if (ability.cooldownTimer.isRunning ||
                (ability is Durationable && ability.durationTimer.isRunning)) {
                abilityPlayer.sendWarningSafely("아직 능력을 사용할 수 없습니다.")
                return
            }

            // 손에 든 아이템 체크
            val itemInHand = event.item
            if (itemInHand == null || itemInHand.type == Material.AIR || !ability.isCastItem(itemInHand)) return

            // 캐스팅
            if (ability.onCastByItem(event, itemInHand, clickType)) {
                if (ability is Durationable) {
                    ability.durationTimer.start()
                } else {
                    ability.cooldownTimer.start()
                }

                abilityPlayer.sendMessageSafely("${ability.name} §e능력을 사용했습니다.")
                Logger.log("${abilityPlayer.name} §e님께서 §f${ability.name} §e능력을 사용했습니다.")
            }
        }
    }

}