package net.pooleaf.abilityreward.services

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.abilityreward.AbilityRewardApi
import net.pooleaf.core.modules.commonsender.CommonSenderModule
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.killstreak.KillStreak
import net.pooleaf.gamecore.player.GamePlayer
import net.pooleaf.gamecore.team.Team
import net.pooleaf.money.MoneyApi
import kotlin.math.floor

class AbilityRewardService {

    fun createCalculateWinMoneyFunction(formula: String) {
        AbilityRewardApi.unsafe.javaScriptService.eval("""
            function calculateWinMoney(startPlayerCount, startTeamCount, teamPlayerCount) {
                return ${formula}
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
        if (killMoney <= 0) return

        // 메시지
        gamePlayer.sendMessageSafely("§a+${killMoney.toInt()}원 (킬)")
        gamePlayer.playSoundSafely(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 1.0F)

        BukkitAsyncScope.launch {
            // 돈 추가
            MoneyApi.addMoney(gamePlayer.uuid, killMoney, CommonSenderModule.getPluginSender())

            // 로그
            Logger.log("킬 게임머니 지급: ${gamePlayer.name}(${gamePlayer.uuid}): ${killMoney} 원")
        }
    }

    fun giveKillStreakMoney(gamePlayer: GamePlayer) {
        val killMoney = when (gamePlayer.killStreak) {
            KillStreak.DOUBLE -> AbilityRewardApi.unsafe.abilityRewardConfig.doubleKillMoney
            KillStreak.TRIPLE -> AbilityRewardApi.unsafe.abilityRewardConfig.tripleKillMoney
            KillStreak.QUADRA -> AbilityRewardApi.unsafe.abilityRewardConfig.quadraKillMoney
            KillStreak.PENTA -> AbilityRewardApi.unsafe.abilityRewardConfig.pentaKillMoney
            null -> return
        }
        if (killMoney <= 0) return

        // 메시지
        gamePlayer.sendMessageSafely("§a+${killMoney.toInt()}원 (연속킬)")
        gamePlayer.playSoundSafely(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 1.0F)

        BukkitAsyncScope.launch {
            // 돈 추가
            MoneyApi.addMoney(gamePlayer.uuid, killMoney, CommonSenderModule.getPluginSender())

            // 로그
            Logger.log("연속킬 게임머니 지급: ${gamePlayer.name}(${gamePlayer.uuid}): ${killMoney} 원")
        }
    }

    fun giveAssistMoney(gamePlayer: GamePlayer) {
        val assistMoney = AbilityRewardApi.unsafe.abilityRewardConfig.assistMoney
        if (assistMoney <= 0) return

        // 메시지
        gamePlayer.sendMessageSafely("§a+${assistMoney.toInt()}원 (어시스트)")
        gamePlayer.playSoundSafely(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 1.0F)

        BukkitAsyncScope.launch {
            // 돈 추가
            MoneyApi.addMoney(gamePlayer.uuid, assistMoney, CommonSenderModule.getPluginSender())

            // 로그
            Logger.log("어시스트 게임머니 지급: ${gamePlayer.name}(${gamePlayer.uuid}): ${assistMoney} 원")
        }
    }

    fun giveWinMoney(team: Team) {
        val winMoney = calculateWinMoney(team)

        team.players.forEach { gamePlayer ->
            // 메시지
            gamePlayer.sendMessageSafely("§a§l+${winMoney.toInt()}원 (우승)")
            // 우승 사운드가 따로 있으므로 사운드는 사용안함

            BukkitAsyncScope.launch {
                // 돈 추가
                MoneyApi.addMoney(gamePlayer.uuid, winMoney, CommonSenderModule.getPluginSender())

                // 로그
                Logger.log("우승 게임머니 지급: ${gamePlayer.name}(${gamePlayer.uuid}): ${winMoney} 원")
            }
        }
    }

}