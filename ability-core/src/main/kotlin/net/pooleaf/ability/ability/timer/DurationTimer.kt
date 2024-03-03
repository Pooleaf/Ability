package net.pooleaf.ability.ability.timer

import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.event.ability.AbilityDurationEndEvent
import net.pooleaf.ability.event.ability.AbilityDurationStartEvent
import net.pooleaf.core.modules.gui.bukkit.actionbar.ActionBar
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.utils.StringUtil
import org.bukkit.Bukkit

open class DurationTimer(
    val ability: Ability,
    timeMillis: Long,
    intervalMillis: Long = 100L
) : SimpleTimer(timeMillis, intervalMillis) {

    override fun onStart() {
        showRemainingTimeActionBar()

        // 이벤트
        Bukkit.getPluginManager().callEvent(AbilityDurationStartEvent(ability.abilityPlayer!!, ability, timeMillis))
    }

    override fun onRun() {
        showRemainingTimeActionBar()

        // 3초 이하일 때 효과음
        val remainingTimeMillis = remainingTimeMillis ?: return
        val remainingTime100Millis = Math.round(remainingTimeMillis.toFloat() / 100)
        if (remainingTimeMillis != null && remainingTime100Millis % 10 == 0 && remainingTime100Millis > 0 && remainingTime100Millis < 40) {
            ability.abilityPlayer?.playSoundSafely(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 1.0F - ((3 - (remainingTime100Millis / 10)) * 0.05F))
        }
    }

    override fun onEnd() {
        ability.abilityPlayer?.player?.let {
            ActionBar.remove(it)
        }

        if (ability is Cooldownable) {
            (ability as Cooldownable).cooldownTimer.start()
        }

        // 종료 효과음
        ability.abilityPlayer?.playSoundSafely(XSound.ENTITY_ITEM_BREAK, 0.4F, 0.5F)

        // 이벤트
        Bukkit.getPluginManager().callEvent(AbilityDurationEndEvent(ability.abilityPlayer!!, ability))
    }

    private fun showRemainingTimeActionBar() {
        ability.abilityPlayer?.player?.let { player ->
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