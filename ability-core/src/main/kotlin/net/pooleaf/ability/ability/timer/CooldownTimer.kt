package net.pooleaf.ability.ability.timer

import com.cryptomorin.xseries.XSound
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
        Bukkit.getPluginManager().callEvent(AbilityCooldownStartEvent(ability.abilityPlayer!!, ability, timeMillis))
    }

    override fun onRun() {
        showRemainingTimeActionBar()

        // 3초 이하일 때 효과음
        val remainingTimeMillis = remainingTimeMillis ?: return
        val remainingTime100Millis = Math.round(remainingTimeMillis.toFloat() / 100)
        if (remainingTimeMillis != null && remainingTime100Millis % 10 == 0 && remainingTime100Millis > 0 && remainingTime100Millis < 40) {
            ability.abilityPlayer?.playSoundSafely(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 1.0F)
        }
    }

    override fun onEnd() {
        showEndActionBar()

        // 종료 효과음
        ability.abilityPlayer?.playSoundSafely(XSound.ENTITY_ARROW_HIT, 0.4F, 1.0F)

        // 이벤트
        Bukkit.getPluginManager().callEvent(AbilityCooldownEndEvent(ability.abilityPlayer!!, ability))
    }

    private fun showRemainingTimeActionBar() {
        ability.abilityPlayer?.player?.let { player ->
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
        ability.abilityPlayer?.player?.let {
            ActionBar.show(it, "§e이제 다시 능력을 사용할 수 있습니다!")
        }
    }

}