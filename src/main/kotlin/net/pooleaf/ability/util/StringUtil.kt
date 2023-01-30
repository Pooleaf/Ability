package net.pooleaf.ability.util

import net.pooleaf.core.modules.support.common.CommonChatColor
import java.util.concurrent.TimeUnit

object StringUtil {

    fun buildTimeStringWithColor(timeMillis: Long, numberColor: CommonChatColor, charColor: CommonChatColor): String {
        val days = TimeUnit.MILLISECONDS.toDays(timeMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(timeMillis) % 24;
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis) % 60;
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis) % 60;

        var string = ""

        string += if (days > 0) "${numberColor}${days}${charColor}일" else ""

        string += if (string.isNotEmpty()) " " else ""
        string += if (hours > 0) "${numberColor}${hours}${charColor}시간" else ""

        string += if (string.isNotEmpty()) " " else ""
        string += if (minutes > 0) "${numberColor}${minutes}${charColor}분" else ""

        string += if (string.isNotEmpty()) " " else ""
        string += if (seconds > 0) "${numberColor}${seconds}${charColor}초" else ""

        return string
    }

}