package net.pooleaf.abilityreplay.data.replays.ability

import net.pooleaf.abilityreplay.data.datas.ability.AbilityCooldownStartData
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class AbilityCooldownStartDataReplayHandler : RecordDataReplayHandler<AbilityCooldownStartData> {

    override fun onPlay(recordData: AbilityCooldownStartData, viewer: Player) {
        // TODO 홀로그램

        val playerName = CommonSenderModule.getOfflinePlayer(recordData.playerUuid)?.displayName ?: recordData.playerUuid.toString()
        viewer.sendMessage("${playerName} §e님이 능력을 사용했습니다. (쿨타임: §f${String.format("%.1f", recordData.cooldownMillis.toFloat() / 1000)}§e초)")
    }

}