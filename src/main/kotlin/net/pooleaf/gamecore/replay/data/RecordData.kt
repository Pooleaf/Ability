package net.pooleaf.gamecore.replay.data

import net.pooleaf.gamecore.replay.replay.ReplayPlayer

interface RecordData {

    val type: String


    fun play(replayPlayer: ReplayPlayer)

}