package net.pooleaf.ability.ability

import net.pooleaf.core.modules.support.common.CommonChatColor

enum class AbilityType(
    val kor: String,
    val color: CommonChatColor
) {

    ACTIVE("액티브", CommonChatColor.RED),
    PASSIVE("패시브", CommonChatColor.GREEN);


    val coloredKor
        get() = "${color}${kor}"

}