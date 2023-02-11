package net.pooleaf.gamecore.v1.sql.dtos

import net.pooleaf.core.modules.channel.ChannelModule
import net.pooleaf.gamecore.v1.game.Game
import java.time.LocalDateTime

data class GameDto(
    val gameId: String,
    val gameTypeId: Int,
    val channelName: String,
    val startedAt: LocalDateTime?,
    val endedAt: LocalDateTime?,
    val cancelYn: String
) {
}

fun Game.toDto(): GameDto {
    return GameDto(
        this.gameId?.toString() ?: error("gameId cannot be null"),
        this.gameTypeId,
        ChannelModule.getCurrentChannelName(),
        this.startedAt,
        this.endedAt,
        if (this.isCancelled) "Y" else "N"
    )
}