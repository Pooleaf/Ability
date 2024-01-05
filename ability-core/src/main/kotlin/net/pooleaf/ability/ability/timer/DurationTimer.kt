package net.pooleaf.ability.ability.timer

import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.event.ability.AbilityCooldownStartEvent
import net.pooleaf.ability.event.ability.AbilityDurationEndEvent
import net.pooleaf.ability.event.ability.AbilityDurationStartEvent
import net.pooleaf.gamecore.utils.StringUtil
import net.pooleaf.core.modules.gui.bukkit.actionbar.ActionBar
import net.pooleaf.core.modules.support.common.CommonChatColor
import org.bukkit.Bukkit

open class DurationTimer(
    val ability: Ability,
    timeMillis: Long,
    intervalMillis: Long = 100L
) : SimpleTimer(timeMillis, intervalMillis) {

    override fun onStart() {
        showRemainingTimeActionBar()

        // 이벤트
        Bukkit.getPluginManager().callEvent(AbilityDurationStartEvent(ability.player!!, ability, timeMillis))
    }

    override fun onRun() {
        showRemainingTimeActionBar()

        // 3초 이하일 때 효과음
        val remainingTimeMillis = remainingTimeMillis
        if (remainingTimeMillis != null && remainingTimeMillis % 1000 < 100 && remainingTimeMillis < 4_000L) {
            ability.player?.playSoundSafely(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 1.0F)
        }
    }

    override fun onEnd() {
        ability.player?.player?.let {
            ActionBar.remove(it)
        }

        if (ability is Cooldownable) {
            (ability as Cooldownable).cooldownTimer.start()
        }

        // 종료 효과음
        // TODO ability.player?.playSoundSafely(XSound.BLOCK_ANVIL_BREAK, 0.4F, 1.0F)

        // 이벤트
        Bukkit.getPluginManager().callEvent(AbilityDurationEndEvent(ability.player!!, ability))
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