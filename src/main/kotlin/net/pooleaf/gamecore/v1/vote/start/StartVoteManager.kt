package net.pooleaf.gamecore.v1.vote.start

import net.pooleaf.gamecore.v1.Broadcaster
import net.pooleaf.gamecore.v1.GameCore
import net.pooleaf.gamecore.v1.player.GamePlayer

class StartVoteManager {

    val startVote = StartVote()
    val startVoteGui = StartVoteGui()


    /**
     * 투표를 초기화합니다.
     */
    fun initVote() {
        startVote.clear()
        startVoteGui.updateAsynchronously()
    }

    /**
     * 플레이어를 시작 투표에 찬성시킵니다.
     */
    fun voteToAgree(gamePlayer: GamePlayer) {
        if (GameCore.game.isCountingStarted) {
            gamePlayer.sendWarningSafely("이미 게임이 시작되었습니다.")
            return
        }

        if (startVote.isAgree(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 투표에 찬성했습니다.")
            return
        }

        startVote.voteToAgree(gamePlayer.uuid)
        gamePlayer.sendMessageSafely("§a시작 투표에 §l찬성§a했습니다.")
        broadcastProgress()

        startVoteGui.updateAsynchronously()

        // 과반수 동의 시 게임 시작
        if (!GameCore.game.isCountingStarted
            && startVote.agreePlayers.size >= GameCore.playerManager.getOnlineJoinedPlayers().size.toFloat() / 2) {
            GameCore.game.start(null)
        }
    }

    /**
     * 플레이어를 시작 투표에 반대시킵니다.
     */
    fun voteToDisagree(gamePlayer: GamePlayer) {
        if (GameCore.game.isCountingStarted) {
            gamePlayer.sendWarningSafely("이미 게임이 시작되었습니다.")
            return
        }

        if (startVote.isDisagree(gamePlayer.uuid)) {
            gamePlayer.sendWarningSafely("이미 투표에 반대했습니다.")
            return
        }

        startVote.voteToDisagree(gamePlayer.uuid)
        gamePlayer.sendMessageSafely("§c시작 투표에 §l반대§c했습니다.")
        broadcastProgress()
    }

    fun broadcastProgress() {
        val agreeCount = startVote.agreePlayers.size
        val disagreeCount = startVote.disagreePlayers.size

        Broadcaster.broadcast("§e게임 시작 투표를 진행 중입니다.. §a찬성: ${agreeCount}§a명 §c반대: ${disagreeCount}§c명")
    }

}