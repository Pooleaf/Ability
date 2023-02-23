package net.pooleaf.gamecore.replay.data

import net.pooleaf.gamecore.replay.replay.ReplayPlayer

interface RecordData {

    val type: String


    fun onPlay(replayPlayer: ReplayPlayer)

    // fun onReverse(replayPlayer: ReplayPlayer)

}