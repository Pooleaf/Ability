package net.pooleaf.abilityreplay.data.replays.ability

import net.pooleaf.abilityreplay.data.datas.ability.AbilityDurationEndData
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class AbilityDurationEndDataReplayHandler : RecordDataReplayHandler<AbilityDurationEndData> {

    override fun onPlay(recordData: AbilityDurationEndData, viewer: Player) {
        // TODO 홀로그램

        val playerName = CommonSenderModule.getOfflinePlayer(recordData.playerUuid)?.displayName ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님의 능력 지속시간이 종료되었습니다.")
    }

}