package net.pooleaf.gamecore.v2.phases

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import net.pooleaf.ability.util.StringUtil
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.v2.Broadcaster
import net.pooleaf.gamecore.v2.GameCore
import net.pooleaf.gamecore.v2.phase.Phase

class WorldBorderUpdatePhase(
    // 새로운 경계선 크기
    val newWorldBorderSize: Int,

    // 경계선 크기가 변하기 전 대기 시간(초) (예: n초 뒤 경계선이 줄어듭니다.)
    val updateWaitSeconds: Int,

    // 초당 경계선이 변화할 크기
    val updateSizePerSeconds: Int
): Phase() {

    override suspend fun onStart() {
        GameCore.currentMap?.let { currentMap ->
            val currentWorldBorderSize = currentMap.currentWorldBorderSize

            // 크기가 변화하지 않을 경우 종료
            if (newWorldBorderSize == currentWorldBorderSize) {
                end()
                return
            }

            val updateMessage = if (newWorldBorderSize > currentWorldBorderSize) {
                "늘어납니다"
            } else {
                "줄어듭니다"
            }

            // 경계선 변화 알림 메시지
            for (count in updateWaitSeconds downTo 1) {
                if (count == updateWaitSeconds || count <= 10) {
                    val updateTime = StringUtil.buildTimeStringWithColor(count * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

                    Broadcaster.broadcast("${updateTime} §e후 맵의 경계가 ${updateMessage}.")
                }
            }

            // 경계선 변화 시작
            val updateDurationSeconds = currentMap.updateWorldBorder(newWorldBorderSize, updateSizePerSeconds)
            val updateDurationTime = StringUtil.buildTimeStringWithColor(updateDurationSeconds * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

            Broadcaster.broadcast("${updateDurationTime} §e동안 맵의 경계가 ${updateMessage}.")

            delay(updateDurationSeconds * 1000L)
        } ?: error("currentMap cannot be null")
    }

    override fun onEnd() {
        super.onEnd()
    }

}