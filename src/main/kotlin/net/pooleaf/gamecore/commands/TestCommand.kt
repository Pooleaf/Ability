package net.pooleaf.gamecore.commands

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.ChunkCoordIntPair
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo
import com.comphenix.protocol.wrappers.WrappedBlockData
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.core.modules.support.bukkit.util.deserializeFromJson
import net.pooleaf.core.modules.support.bukkit.util.serializeToJson
import net.pooleaf.core.modules.support.common.util.GsonUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.GameCorePermission
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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

        sender.sendMessage("${record.replay.gameId} 녹화를 중지했습니다.")
    }

    @Command(
        parent = ["gtest"],
        name = ["startReplay"],
        arguments = "(gameId)",
        description = "replay",
        permission = GameCorePermission.ADMIN,
        async = false
    )
    fun game_test_startReplay(player: Player, result: CommandResult) {
        if (result.argumentsLength > 0) {
            val gameId = result.getArgument(0)
            val replay = GameCore.unsafe.replayService.loadReplayFromDatabase(UUID.fromString(gameId))

            if (replay == null) {
                player.sendWarning("리플레이가 존재하지 않습니다.")
                return
            }

            GameCore.unsafe.replayService.playReplay(player, replay.gameId)

            player.sendMessage("${replay.gameId} 리플레이를 시작합니다.")
        } else {
            if (GameCore.unsafe.recordManager.record == null) {
                player.sendWarning("녹화가 없습니다.")
                return
            }

            val record = GameCore.unsafe.recordManager.record!!
            val replayUuid = record.replay.gameId

            GameCore.unsafe.replayService.playReplay(player, replayUuid)

            player.sendMessage("${replayUuid} 리플레이를 시작합니다.")
        }
    }

    @Command(
        parent = ["gtest"],
        name = ["exitReplay"],
        description = "replay",
        permission = GameCorePermission.ADMIN,
        async = false
    )
    fun game_test_exitReplay(player: Player, result: CommandResult) {
        if (!GameCore.unsafe.replayService.isPlayingReplay(player)) {
            player.sendWarning("리플레이 재생 중이 아닙니다.")
            return
        }

        val replayPlayer = GameCore.unsafe.replayPlayerManager.get(player.uniqueId)
        GameCore.unsafe.replayService.exitReplay(player)

        player.sendMessage("${replayPlayer.replay.gameId} 리플레이를 종료합니다.")
    }

    @Command(
        parent = ["gtest"],
        name = ["jumpTo"],
        arguments = "<Tick>",
        description = "replay jump to",
        permission = GameCorePermission.ADMIN,
    )
    fun game_test_jumpTo(player: Player, result: CommandResult) {
        if (!GameCore.unsafe.replayService.isPlayingReplay(player)) {
            player.sendWarning("리플레이 재생 중이 아닙니다.")
            return
        }

        val tick = result.getArgumentAsLong(0)
        if (tick == null) {
            player.sendWarning("Tick은 정수만 입력할 수 있습니다.")
            return
        }

        val replayPlayer = GameCore.unsafe.replayPlayerManager.get(player.uniqueId)
        if (tick > replayPlayer.replay.endTick) {
            player.sendWarning("Tick이 리플레이 길이보다 큽니다. (리플레이 길이: ${replayPlayer.replay.endTick})")
            return
        }

        if (replayPlayer.isRunning()) {
            replayPlayer.pause()
        }

        replayPlayer.jumpTo(tick)
        replayPlayer.play()

        player.sendMessage("${tick} Tick을 재생합니다.")
    }

    @Command(
        parent = ["gtest"],
        name = ["multiBlockChange"],
        description = "multi block change",
        permission = GameCorePermission.ADMIN,
    )
    fun game_test_multiBlockChange(player: Player, result: CommandResult) {
        var chunkBlocks = hashMapOf<ChunkCoordIntPair, ArrayList<MultiBlockChangeInfo>>()

        val chunk = player.world.getChunkAt(player.location)
        val chunkPair = ChunkCoordIntPair(chunk.x, chunk.z)
        for (x in 0 until 16) {
            for (y in 0 until 256) {
                for (z in 0 until 16) {
                    val location = Location(player.world, chunk.x * 16 + x.toDouble(), y.toDouble(), chunk.z * 16 + z.toDouble())
                    val wrappedBlockData = WrappedBlockData.createData(Material.AIR, 0)
                    val multiBlockChangeInfo = MultiBlockChangeInfo(location, wrappedBlockData)

                    var blocks = chunkBlocks.get(chunkPair)
                    if (blocks == null) {
                        blocks = arrayListOf()
                        chunkBlocks.put(chunkPair, blocks)
                    }

                    blocks.add(multiBlockChangeInfo)

                    println("location $location")
                }
            }
        }

        chunkBlocks.forEach { chunk, multiBlockChangeInfos ->
            val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.MULTI_BLOCK_CHANGE)
            packet.chunkCoordIntPairs.write(0, chunk)
            packet.multiBlockChangeInfoArrays.write(0, multiBlockChangeInfos.toTypedArray())
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)

            println("send")
        }
    }

    @Command(
        parent = ["gtest"],
        name = ["refreshChunk"],
        description = "refresh current chunk",
        permission = GameCorePermission.ADMIN,
    )
    fun game_test_refreshChunk(player: Player, result: CommandResult) {
        val chunk = player.world.getChunkAt(player.location)
        player.world.refreshChunk(chunk.x, chunk.z)

        player.sendMessage("refresh chunk")
    }

    @Command(
        parent = ["gtest"],
        name = ["visualBlockChunk"],
        description = "get visual block chunk",
        permission = GameCorePermission.ADMIN,
    )
    fun game_test_visualBlockChunk(player: Player, result: CommandResult) {
        val chunk = player.world.getChunkAt(player.location)

        if (!GameCore.unsafe.replayService.isPlayingReplay(player)) {
            player.sendWarning("리플레이 재생 중이 아닙니다.")
            return
        }

        val replayPlayer = GameCore.unsafe.replayPlayerManager.get(player.uniqueId)
        val blocks = replayPlayer.virtualBlockManager.getByChunk(chunk.x, chunk.z)

        player.sendMessage("blocks: ${blocks}")
    }


    var serializeJson: String? = null

    @Command(
        parent = ["gtest"],
        name = ["convertToString"],
        description = "convert itemstack to string",
        permission = GameCorePermission.ADMIN,
    )
    fun game_test_convertToString(player: Player, result: CommandResult) {
        val itemInHand = player.itemInHand

        serializeJson = itemInHand.serializeToJson()

        val gsonTest = GsonUtil.getGson().toJson(itemInHand)

        println(serializeJson)
        println(gsonTest)
    }

    @Command(
        parent = ["gtest"],
        name = ["convertFromString"],
        description = "convert itemstack from string",
        permission = GameCorePermission.ADMIN,
    )
    fun game_test_converFromString(player: Player, result: CommandResult) {
        val item = serializeJson?.deserializeFromJson() as ItemStack?

        player.inventory.addItem(item)
        player.updateInventory()
    }

}