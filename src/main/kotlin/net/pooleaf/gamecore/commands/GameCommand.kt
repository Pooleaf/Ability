package net.pooleaf.gamecore.commands

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.Broadcaster
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.GameCorePermission
import org.bukkit.command.CommandSender

class GameCommand {

    companion object {

        @Command(
            name = ["게임", "game"],
            helpCommand = true,
            color = CommonChatColor.AQUA,
            permission = GameCorePermission.ADMIN
        )
        fun game(sender: CommandSender?, result: HelpCommandResult?) {
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
                sender.warning("이미 게임이 시작되었습니다.")
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
                sender.warning("아직 게임 중이 아닙니다.")
                return
            }

            GameCore.game.cancel()
            Broadcaster.broadcast("${sender.displayName} §b님께서 게임을 중단시켰습니다.")
        }

    }

}