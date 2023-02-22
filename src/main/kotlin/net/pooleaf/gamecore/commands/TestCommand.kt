package net.pooleaf.gamecore.commands

import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.GameCorePermission
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

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

    @Command(
        parent = ["gtest"],
        name = ["startRecord"],
        description = "start record",
        permission = GameCorePermission.ADMIN
    )
    fun game_test_startRecord(sender: CommandSender, result: CommandResult) {
        if (GameCore.unsafe.recordManager.isRecording()) {
            sender.sendWarning("이미 녹화 중입니다.")
            return
        }

        val uuid = UUID.randomUUID()
        val targetPlayerUuid = GameCore.unsafe.playerManager.getJoinedPlayers().map { it.uuid }
        GameCore.unsafe.recordManager.startRecord(uuid, targetPlayerUuid)

        sender.sendMessage("${uuid} 녹화를 시작했습니다.")
    }

    @Command(
        parent = ["gtest"],
        name = ["stopRecord"],
        description = "stop record",
        permission = GameCorePermission.ADMIN
    )
    fun game_test_stopRecord(sender: CommandSender, result: CommandResult) {
        if (!GameCore.unsafe.recordManager.isRecording()) {
            sender.sendWarning("녹화 중이 아닙니다.")
            return
        }

        val record = GameCore.unsafe.recordManager.record!!
        GameCore.unsafe.recordManager.endRecord()

        sender.sendMessage("${record.replay.uuid} 녹화를 중지했습니다.")
    }

    @Command(
        parent = ["gtest"],
        name = ["replay"],
        description = "replay",
        permission = GameCorePermission.ADMIN,
        async = false
    )
    fun game_test_replay(player: Player, result: CommandResult) {
        if (GameCore.unsafe.recordManager.record == null) {
            player.sendWarning("녹화가 없습니다.")
            return
        }


        val record = GameCore.unsafe.recordManager.record!!
        val replayUuid = record.replay.uuid

        GameCore.unsafe.replayService.playReplay(player, replayUuid)

        player.sendMessage("${replayUuid} 리플레이를 시작합니다.")
    }

}