package net.pooleaf.ability.ability.timer

import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.event.ability.AbilityCooldownEndEvent
import net.pooleaf.ability.event.ability.AbilityCooldownStartEvent
import net.pooleaf.core.modules.gui.bukkit.actionbar.ActionBar
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.utils.StringUtil
import org.bukkit.Bukkit

open class CoolDownTimer(
    val ability: Ability,
    timeMillis: Long
) : SimpleTimer(timeMillis, 100L) {

    override fun onStart() {
        showRemainingTimeActionBar()

        // 이벤트
        Bukkit.getPluginManager().callEvent(AbilityCooldownStartEvent(ability.player!!, ability, timeMillis))
    }

    override fun onRun() {
        showRemainingTimeActionBar()
    }

    override fun onEnd() {
        showEndActionBar()

        // 이벤트
        Bukkit.getPluginManager().callEvent(AbilityCooldownEndEvent(ability.player!!, ability))
    }

    private fun showRemainingTimeActionBar() {
        ability.player?.player?.let { player ->
            val time = remainingTimeMillis?.let { remainingTimeMillis ->
                when {
                    remainingTimeMillis <= 0 -> 0
                    // 10초보다 작으면 소수점까지 표기
                    remainingTimeMillis < 10_000L -> "${String.format("%.1f", remainingTimeMillis.toFloat() / 1000)}§e초"
                    // 10보다 크면 시분초 표기
                    else -> StringUtil.buildTimeStringWithColor(remainingTimeMillis, CommonChatColor.WHITE, CommonChatColor.YELLOW)
                }
            }?: "?"

            ActionBar.show(player, "§e능력 재사용 대기 시간 §f${time}")
        }
    }


    private fun showEndActionBar() {
        ability.player?.player?.let {
            ActionBar.show(it, "§e이제 다시 능력을 사용할 수 있습니다!")
        }
    }

}