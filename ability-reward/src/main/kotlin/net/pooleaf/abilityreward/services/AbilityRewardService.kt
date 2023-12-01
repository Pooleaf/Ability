package net.pooleaf.abilityreward.services

import com.cryptomorin.xseries.XSound
import net.pooleaf.abilityreward.AbilityRewardApi
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.team.Team
import net.pooleaf.money.MoneyApi
import kotlin.math.floor

class AbilityRewardService {

    fun createCalculateWinMoneyFunction(formula: String) {
        AbilityRewardApi.unsafe.javaScriptService.eval("""
            function calculateWinMoney(startPlayerCount, startTeamCount, teamPlayerCount) {
                ${formula}
            }
        """.trimIndent())
    }

    fun calculateWinMoney(winnerTeam: Team): Double {
        // 게임 시작할 때 인원
        val startPlayerCount = GameCore.unsafe.playerManager.getJoinedPlayers().size

        // 게임 시작할 때 팀 개수
        val startTeamCount = GameCore.unsafe.teamManager.teams.size

        // 우승한 팀의 플레이어 수
        val teamPlayerCount = winnerTeam.players.size

        // 계산
        var calculatedMoney = AbilityRewardApi.unsafe.javaScriptService.callFunction("calculateWinMoney", startPlayerCount, startTeamCount, teamPlayerCount) as Double
        // 소수점 정리하기 위해 버림
        calculatedMoney = floor(calculatedMoney)

        return calculatedMoney
    }

    fun giveKillMoney(gamePlayer: GamePlayer) {
        val killMoney = AbilityRewardApi.unsafe.abilityRewardConfig.killMoney
        MoneyApi.addMoney(gamePlayer.uuid, killMoney, CommonSenderModule.getPluginSender())

        // 메시지
        gamePlayer.sendMessageSafely("")
        gamePlayer.sendMessageSafely("§a+ ${killMoney.toInt()}원 (킬 보상)")
        gamePlayer.playSoundSafely(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 1.0F)

        // 로그
        Logger.log("[킬 게임머니 지급] ${gamePlayer.name}(${gamePlayer.uuid}): ${killMoney}")
    }

    fun giveWinMoney(team: Team) {
        val winMoney = calculateWinMoney(team)
        val winMoneyPerPlayer = floor(winMoney / team.players.size)

        team.players.forEach { gamePlayer ->
            MoneyApi.addMoney(gamePlayer.uuid, winMoneyPerPlayer, CommonSenderModule.getPluginSender())

            // 메시지
            gamePlayer.sendMessageSafely("")
            gamePlayer.sendMessageSafely("§a§l+ ${winMoneyPerPlayer.toInt()}원 (우승 보상)")
            // 우승 사운드가 따로 있으므로 사운드는 사용안함

            // 로그
            Logger.log("[우승 게임머니 지급] ${gamePlayer.name}(${gamePlayer.uuid}): ${winMoneyPerPlayer}")
        }
    }

}