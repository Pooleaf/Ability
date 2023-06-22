package net.pooleaf.abilityreplay.data.replays.ability

import net.pooleaf.abilityreplay.data.datas.ability.AbilityDurationStartData
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class AbilityDurationStartDataReplayHandler : RecordDataReplayHandler<AbilityDurationStartData> {

    override fun onPlay(recordData: AbilityDurationStartData, viewer: Player) {
        // TODO 홀로그램

        val playerName = CommonSenderModule.getOfflinePlayer(recordData.playerUuid)?.displayName ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님의 능력 지속시간이 시작되었습니다. (지속시간: §f${String.format("%.1f", recordData.durationMillis.toFloat() / 1000)}§e초)")
    }

}