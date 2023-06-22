package net.pooleaf.abilityreplay.data.replays.ability

import net.pooleaf.abilityreplay.data.datas.ability.AbilityAssignData
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class AbilityAssignDataReplayHandler : RecordDataReplayHandler<AbilityAssignData> {

    override fun onPlay(recordData: AbilityAssignData, viewer: Player) {
        // TODO 홀로그램

        val playerName = CommonSenderModule.getOfflinePlayer(recordData.playerUuid)?.displayName ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님의 능력: §f${recordData.abilityName}")
    }

}