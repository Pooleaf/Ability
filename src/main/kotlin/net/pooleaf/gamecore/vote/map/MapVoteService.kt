package net.pooleaf.gamecore.vote.map

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.map.GameMap
import net.pooleaf.gamecore.player.GamePlayer

class MapVoteService {

    val mapVote = MapVote()
    val mapVoteGui = MapVoteGui()


    fun initVote() {
        mapVote.clear()
        mapVoteGui.updateAsynchronously()
    }

    fun voteTo(gamePlayer: GamePlayer, map: GameMap) {
        if (GameCore.game.countingStarted) {
            gamePlayer.sendWarningSafely("이미 게임이 시작되었습니다.")
            return
        }

        if (mapVote.votedMap.get(gamePlayer.uuid)?.let { it == map } == true) {
            gamePlayer.sendWarningSafely("이미 ${map.displayName} 맵에 투표했습니다.")
            return
        }

        mapVote.voteTo(gamePlayer.uuid, map)
        GameCore.game.map = mapVote.getMostVotedMap()
        gamePlayer.sendMessageSafely("${map.displayName} §e맵에 투표했습니다.")

        mapVoteGui.updateAsynchronously()
    }

    fun voteToRandom(gamePlayer: GamePlayer) {
        if (!mapVote.votedMap.containsKey(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 랜덤에 투표했습니다.")
            return
        }

        mapVote.unvote(gamePlayer.uuid)
        gamePlayer.sendMessageSafely("랜덤§e에 투표했습니다.")

        mapVoteGui.updateAsynchronously()
    }

}