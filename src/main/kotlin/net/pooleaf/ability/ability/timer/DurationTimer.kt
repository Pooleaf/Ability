package net.pooleaf.ability.ability.timer

import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.util.StringUtil
import net.pooleaf.core.modules.gui.bukkit.actionbar.ActionBar
import net.pooleaf.core.modules.support.common.CommonChatColor

open class DurationTimer(
    val ability: Ability,
    timeMillis: Long,
    intervalMillis: Long = 100L
) : SimpleTimer(timeMillis, intervalMillis) {

    override fun onStart() {
        showRemainingTimeActionBar()
    }

    override fun onRun() {
        showRemainingTimeActionBar()
    }

    override fun onEnd() {
        ability.player?.player?.let {
            ActionBar.remove(it)
        }

        if (ability is Cooldownable) {
            (ability as Cooldownable).cooldownTimer.start()
        }
    }

    private fun showRemainingTimeActionBar() {
        ability.player?.player?.let { player ->
            val time = remainingTimeMillis?.let { remainingTimeMillis ->
                when {
                    remainingTimeMillis <= 0 -> 0
                    // 10초보다 작으면 소수점까지 표기
                    remainingTimeMillis < 10_000L -> "${String.format("%.1f", remainingTimeMillis.toFloat() / 1000)}§e초"
                    // 10보다 크면 시분초 표기
                    else -> StringUtil.buildTimeStringWithColor(remainingTimeMillis, CommonChatColor.WHITE, CommonChatColor.GREEN)
                }
            }?: "?"

            ActionBar.show(player, "§a능력 지속 시간 §f${time}")
        }
    }

}