package net.pooleaf.gamecore.replay.replay

import net.pooleaf.gamecore.replay.data.RecordData

interface RecordDataReplayHandler<T : RecordData> {

    fun onPlay(replayPlayer: ReplayPlayer, recordData: T, tick: Long)

    fun onReversePlay(replayPlayer: ReplayPlayer, recordData: T, tick: Long)

    fun onReplayStart(replay: Replay) {}

    fun onReplayExit(replay: Replay) {}

}