package net.pooleaf.gamecore.commands

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.GameCorePermission
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
        if (GameCore.game.countingStarted) {
            sender.nwarning("이미 게임이 시작되었습니다.")
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
        if (!GameCore.game.countingStarted) {
            sender.nwarning("아직 게임 중이 아닙니다.")
            return
        }

        GameCore.game.cancel()
        Broadcaster.broadcast("${sender.displayName} §b님께서 게임을 중단시켰습니다.")
    }

    @Command(
        parent = ["", "게임"],
        name = ["관전", "rhkswjs", "observer"],
        description = "관전 모드로 전환하거나 해제합니다."
    )
    fun game_observer(player: CommonPlayer<Player>, result: CommandResult?) {
        // 대기 중에만 사용 가능. 단 관리자는 아무 때나 사용할 수 있음
        if (GameCore.game.countingStarted && !player.platformSender.isOp) {
            player.nwarning("게임 중에는 사용할 수 없습니다.")
            return
        }

        val gamePlayer = GameCore.playerManager.get(player.uuid)

        // 관전 모드로 전환
        if (!gamePlayer.observer) {
            gamePlayer.toggleObserver(true)
            gamePlayer.joined = false
            Broadcaster.broadcast("§f${gamePlayer.displayName} §b님께서 관전을 시작했습니다.")
        }
        // 관전 모드 해제
        else {
            // 관리자는 게임 중에도 관전을 해제할 수 있으나, 게임 종료 후에는 불가능
            if (GameCore.game.ended) {
                player.nwarning("게임이 종료되어 관전을 해제할 수 없습니다.")
                return
            }

            gamePlayer.toggleObserver(false)
            gamePlayer.joined = true
            Broadcaster.broadcast("§f${gamePlayer.displayName} §b님께서 관전을 종료했습니다.")

            // 게임을 끝낼 수 있으면 끝냄
            if (GameCore.game.gameStarted && GameCore.game.canEnd()) {
                GameCore.game.end()
            }
        }

        // 대기 중일 경우 대기 액션바 업데이트
        if (!GameCore.game.countingStarted) {
            Broadcaster.broadcastWaitingActionBar()
        }
    }

}