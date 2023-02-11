package net.pooleaf.gamecore.v1.vote.map

import net.pooleaf.gamecore.v1.GameCore
import net.pooleaf.gamecore.v1.map.GameMap
import net.pooleaf.gamecore.v1.player.GamePlayer

class MapVoteManager {

    val mapVote = MapVote()
    val mapVoteGui = MapVoteGui()


    fun initVote() {
        mapVote.clear()
        mapVoteGui.updateAsynchronously()
    }

    fun voteTo(gamePlayer: GamePlayer, map: GameMap) {
        if (GameCore.game.isCountingStarted) {
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