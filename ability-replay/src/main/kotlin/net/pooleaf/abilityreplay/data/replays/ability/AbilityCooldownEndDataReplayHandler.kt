package net.pooleaf.abilityreplay.data.replays.ability

import net.pooleaf.abilityreplay.data.datas.ability.AbilityCooldownEndData
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class AbilityCooldownEndDataReplayHandler : RecordDataReplayHandler<AbilityCooldownEndData> {

    override fun onPlay(recordData: AbilityCooldownEndData, viewer: Player) {
        // TODO 홀로그램

        val playerName = CommonSenderModule.getPlayer(recordData.playerUuid)?.displayName ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님의 능력 쿨타임이 종료되었습니다.")
    }

}