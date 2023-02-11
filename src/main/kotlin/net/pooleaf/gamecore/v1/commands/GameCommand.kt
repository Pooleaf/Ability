package net.pooleaf.gamecore.v1.commands

import kotlinx.coroutines.launch
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.v1.Broadcaster
import net.pooleaf.gamecore.v1.GameCore
import net.pooleaf.gamecore.v1.GameCorePermission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GameCommand {

    @Command(
        name = ["게임", "game"],
        helpCommand = true,
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun game(sender: CommandSender, result: HelpCommandResult) {
    }

    @Command(
        parent = ["게임"],
        name = ["시작", "start"],
        description = "게임을 시작시킵니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun game_start(sender: CommonCommandSender<CommandSender>, result: CommandResult?) {
        if (GameCore.game.isCountingStarted) {
            sender.sendWarning("이미 게임이 시작되었습니다.")
            return
        }

        GameCore.game.start(sender.platformSender)
        Broadcaster.broadcast("${sender.displayName} §b님께서 게임을 시작시켰습니다.")
    }

    @Command(
        parent = ["게임"],
        name = ["중단", "중지", "stop"],
        description = "게임을 중단시킵니다.",
        color = CommonChatColor.AQUA,
        permission = GameCorePermission.ADMIN
    )
    fun game_stop(sender: CommonCommandSender<CommandSender>, result: CommandResult?) {
        if (!GameCore.game.isCountingStarted) {
            sender.sendWarning("아직 게임 중이 아닙니다.")
            return
        }

        BukkitAsyncScope.launch {
            GameCore.game.cancel()
            Broadcaster.broadcast("${sender.displayName} §b님께서 게임을 중단시켰습니다.")
        }
    }

    @Command(
        parent = ["", "게임"],
        name = ["관전", "rhkswjs", "observer"],
        description = "관전 모드로 전환하거나 해제합니다.",
        async = false
    )
    fun game_observer(player: CommonPlayer<Player>, result: CommandResult?) {
        // 대기 중에만 사용 가능. 단 관리자는 아무 때나 사용할 수 있음
        if (GameCore.game.isGameStarted && !player.platformSender.isOp) {
            player.sendWarning("게임 중에는 사용할 수 없습니다.")
            return
        }

        val gamePlayer = GameCore.playerManager.get(player.uuid)

        BukkitAsyncScope.launch {
            // 관전 모드로 전환
            if (!gamePlayer.observer) {
                // 게임 카운팅 중 인원이 적으면 관전 전환 불가
                if (GameCore.game.isCountingStarted && GameCore.playerManager.getOnlinePlayingPlayers().size <= 2 && !player.platformSender.isOp) {
                    player.sendWarning("인원이 적어 관전 모드로 전환할 수 없습니다.")
                    return@launch
                }

                gamePlayer.toggleObserver(true)
                gamePlayer.joined = false
                Broadcaster.broadcast("§f${gamePlayer.displayName} §b님께서 관전을 시작했습니다.")

                GameCore.game.onPlayerLeft()
            }
            // 관전 모드 해제
            else {
                // 관리자는 게임 중에도 관전을 해제할 수 있으나, 게임 종료 후에는 불가능
                if (GameCore.game.isEnded) {
                    player.sendWarning("게임이 종료되어 관전을 해제할 수 없습니다.")
                    return@launch
                }

                gamePlayer.toggleObserver(false)
                gamePlayer.joined = true
                Broadcaster.broadcast("§f${gamePlayer.displayName} §b님께서 관전을 종료했습니다.")

                GameCore.game.onPlayerJoin()
            }

            // 대기 중일 경우 대기 액션바 업데이트
            BukkitSyncScope.launch {
                if (!GameCore.game.isCountingStarted) {
                    Broadcaster.broadcastWaitingActionBar()
                }
            }
        }
    }

    @Command(
        parent = ["", "게임"],
        name = ["플레이어목록", "playerList", "list"],
        description = "게임에 참여 중인 플레이어 목록을 확인합니다."
    )
    fun game_playerList(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        sender.sendMessage("")

        // 참여자
        var players = when {
            // 대기 중
            !GameCore.game.isGameStarted -> GameCore.playerManager.getOnlinePlayingPlayers().map { it.displayName }.joinToString(", ")
            // 게임 중
            else -> GameCore.teamManager.teams.map { team ->
                val teamPlayers = team.players.map { gamePlayer ->
                    var name = gamePlayer.displayName

                    // 오프라인일 경우 회색
                    if (!gamePlayer.isOnline) {
                        name = "§7${gamePlayer.displayName}"
                    }

                    // 탈락 표시
                    if (gamePlayer.defeated) {
                        name = "§7${gamePlayer.displayName}(탈락)"
                    }

                    name
                }.joinToString(", ")

                // 팀이 여러명일 경우 괄호로 묶어줌
                if (GameCore.teamConfig.playerCountPerTeam == 1) {
                    teamPlayers
                } else {
                    // 탈락한 팀은 회색
                    if (team.isDefeated()) {
                        "§7($teamPlayers)"
                    } else {
                        "($teamPlayers)"
                    }
                }
            }.joinToString(", ")
        }
        var playerCount = GameCore.playerManager.getOnlinePlayingPlayers().size
        sender.sendMessage("§c참여자($playerCount): §f$players")

        // 관전자
        if (!GameCore.playerManager.getObservers().isEmpty()) {
            val observerPlayers = GameCore.playerManager.getObservers().map { it.displayName }.joinToString(", ")
            val observerCount = GameCore.playerManager.getObservers().size
            sender.sendMessage("§b관전자($observerCount): §f${observerPlayers}")
        }
    }

}