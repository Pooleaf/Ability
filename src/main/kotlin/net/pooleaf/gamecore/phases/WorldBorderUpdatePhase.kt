package net.pooleaf.gamecore.phases

import kotlinx.coroutines.delay
import net.pooleaf.ability.util.StringUtil
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase

abstract class WorldBorderUpdatePhase(): Phase() {

    /**
     * 새로운 경계선 크기
     */
    abstract fun getNewWorldBorderSize(): Int

    /**
     * 경계선 크기가 변하기 전 대기 시간(초) (예: n초 뒤 경계선이 줄어듭니다.)
     */
    abstract fun getUpdateWaitSeconds(): Int

    /**
     * 초당 경계선이 변화할 크기
     */
    abstract fun getUpdateSizePerSeconds(): Int


    override suspend fun onStart() {
        GameCore.currentMap?.let { currentMap ->
            val currentWorldBorderSize = currentMap.currentWorldBorderSize

            // 크기가 변화하지 않을 경우 종료
            if (getNewWorldBorderSize() == currentWorldBorderSize) {
                end()
                return
            }

            val updateMessage = if (getNewWorldBorderSize() > currentWorldBorderSize) {
                "늘어납니다"
            } else {
                "줄어듭니다"
            }

            // 경계선 변화 알림 메시지
            for (count in getUpdateWaitSeconds() downTo 1) {
                if (count == getUpdateWaitSeconds() || count <= 10) {
                    val updateTime = StringUtil.buildTimeStringWithColor(count * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

                    Broadcaster.broadcast("${updateTime} §e후 맵의 경계가 ${updateMessage}.")
                }

                delay(1000L)
            }

            // 경계선 변화 시작
            val updateDurationSeconds = currentMap.updateWorldBorder(getNewWorldBorderSize(), getUpdateSizePerSeconds())
            val updateDurationTime = StringUtil.buildTimeStringWithColor(updateDurationSeconds * 1000L, CommonChatColor.WHITE, CommonChatColor.YELLOW)

            Broadcaster.broadcast("${updateDurationTime} §e동안 맵의 경계가 ${updateMessage}.")

            delay(updateDurationSeconds * 1000L)
        } ?: error("currentMap cannot be null")
    }

    override fun onEnd() {
        super.onEnd()
    }

}