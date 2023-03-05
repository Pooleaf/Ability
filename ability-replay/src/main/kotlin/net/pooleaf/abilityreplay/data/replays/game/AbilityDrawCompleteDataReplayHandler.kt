package net.pooleaf.abilityreplay.data.replays.game

import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.gamereplay.replay.RecordDataReplayHandler
import org.bukkit.entity.Player

class AbilityDrawCompleteDataReplayHandler : RecordDataReplayHandler<net.pooleaf.abilityreplay.data.datas.game.AbilityDrawCompleteData> {

    override fun onPlay(recordData: net.pooleaf.abilityreplay.data.datas.game.AbilityDrawCompleteData, viewer: Player) {
        BukkitBroadcaster.broadcast("§e모든 플레이어가 능력을 확정했습니다.")
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_PLAYER_LEVELUP, 0.4F, 1.0F)
    }

}