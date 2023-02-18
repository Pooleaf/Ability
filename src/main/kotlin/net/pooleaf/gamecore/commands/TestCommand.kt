package net.pooleaf.gamecore.commands

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.GameCorePermission
import org.bukkit.command.CommandSender

class TestCommand {

    @Command(
        name = ["gtest"],
        helpCommand = true,
        permission = GameCorePermission.ADMIN
    )
    fun game_test(sender: CommandSender, result: HelpCommandResult) {
    }

    @Command(
        parent = ["gtest"],
        name = ["currentPhase"],
        description = "show current phase",
        permission = GameCorePermission.ADMIN
    )
    fun game_test_currentPhase(sender: CommandSender, result: CommandResult) {
        sender.sendMessage("Current Phase: ${GameCore.game.phasePipeline.currentPhase?.javaClass?.name}")
    }

}