package net.pooleaf.ability.commands

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPermission
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.commonsender.common.CommonPlayer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phases.GodModePhase
import net.pooleaf.gamecore.team.Team
import org.bukkit.command.CommandSender

class AdminAbilityCommand {

    @Command(
        parent = ["능력자"],
        name = ["강제확정", "skip"],
        description = "능력을 강제로 확정시킵니다.",
        color = CommonChatColor.AQUA,
        permission = AbilityPermission.ADMIN
    )
    fun ability_drawSkip(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val currentPhase = AbilityApi.game.phasePipeline.currentPhase
        if (currentPhase !is AbilityDrawPhase) {
            sender.sendWarning("능력 추첨 중이 아닙니다.")
            return
        }

        if (!AbilityApi.unsafe.playerManager.getPlayingPlayers().any { it.tempAbility != null }) {
            sender.sendWarning("능력 추첨 중이 아닙니다.")
            return
        }

        AbilityApi.unsafe.abilityService.skipAbilityDraw()

        BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 모든 플레이어의 능력을 강제로 확정시켰습니다.")
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
    }

    @Command(
        parent = ["능력자"],
        name = ["무적해제", "무적스킵", "skipGodMode", "go"],
        description = "무적을 해제시킵니다.",
        color = CommonChatColor.AQUA,
        permission = AbilityPermission.ADMIN
    )
    fun ability_skipGodMode(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val currentPhase = AbilityApi.game.phasePipeline.currentPhase
        if (currentPhase == null || currentPhase !is GodModePhase || !currentPhase.isStarted || currentPhase.isEnded) {
            sender.sendWarning("무적 시간이 아닙니다.")
            return
        }

        AbilityApi.unsafe.abilityService.skipGodModePhase()

        BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 무적 시간을 종료시켰습니다.")
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
    }

    @Command(
        parent = ["능력자"],
        name = ["능력확인", "checkAbility"],
        arguments = "(플레이어)",
        description = "모든 플레이어 또는 특정 플레이어의 능력을 확인하고 알립니다.",
        color = CommonChatColor.AQUA,
        permission = AbilityPermission.ADMIN
    )
    fun ability_checkAbility(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!AbilityApi.game.isGameStarted) {
            sender.sendMessage("아직 게임 중이 아닙니다.")
            return
        }

        // 특정 플레이어 조회
        if (result.argumentsLength > 0) {
            val targetGamePlayer = AbilityApi.unsafe.playerManager.getByName(result.getArgument(0))
            if (targetGamePlayer == null) {
                sender.sendWarning("존재하지 않는 플레이어입니다.")
                return
            }
            if (!targetGamePlayer.isJoined) {
                sender.sendWarning("게임에 참여하지 않은 플레이어입니다.")
                return
            }
            if (targetGamePlayer.ability == null) {
                sender.sendMessage("능력이 할당되지 않았습니다.")
                return
            }

            val ability = targetGamePlayer.ability!!
            sender.sendMessage("${targetGamePlayer.name} §b님의 능력: §f${ability.fullName}")

            BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 §f${targetGamePlayer.displayName} §b님의 능력을 조회했습니다.")
            BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
        }
        // 모든 플레이어 조회
        else {
            AbilityApi.unsafe.playerManager.getJoinedPlayers().forEach {
                sender.sendMessage("${it.name} §b님의 능력: §f${it.ability?.fullName ?: "없음"}")
            }

            BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 모든 플레이어§b의 능력을 조회했습니다.")
            BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
        }
    }

    @Command(
        parent = ["능력자"],
        name = ["할당", "assign", "setAbility"],
        arguments = "<플레이어> <능력이름|플러그인:능력이름>",
        description = "플레이어에게 능력을 할당합니다.",
        color = CommonChatColor.AQUA,
        permission = AbilityPermission.ADMIN
    )
    fun ability_setAbility(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!AbilityApi.game.isGameStarted) {
            sender.sendWarning("아직 게임이 시작되지 않았습니다.")
            return
        }

        val targetGamePlayer = AbilityApi.unsafe.playerManager.getByName(result.getArgument(0))
        if (targetGamePlayer == null) {
            sender.sendWarning("존재하지 않는 플레이어입니다.")
            return
        }
        if (!targetGamePlayer.isJoined) {
            sender.sendWarning("게임에 참여하지 않은 플레이어입니다.")
            return
        }

        // 능력을 풀네임으로 먼저 검색해보고 없으면 능력 이름으로만 검색
        var targetAbility = AbilityApi.unsafe.abilityManager.getAbilityByFullName(result.subArgument(1))
        if (targetAbility == null) {
            targetAbility = AbilityApi.unsafe.abilityManager.getAbility(result.subArgument(1))
        }

        if (targetAbility == null) {
            sender.sendWarning("존재하지 않는 능력입니다.")
            return
        }
        if (targetGamePlayer.ability == targetAbility) {
            sender.sendWarning("이미 플레이어에게 할당된 능력입니다.")
            return
        }

        if (targetGamePlayer.ability != null) {
            targetGamePlayer.resignAbility()
        }
        targetGamePlayer.assignAbility(targetAbility)

        BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 §f${targetGamePlayer.displayName} §b님께 능력을 할당했습니다.")
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
    }

    @Command(
        parent = ["능력자"],
        name = ["전체할당", "assignAll", "setAbilityAll"],
        arguments = "<능력|플러그인:능력이름>",
        description = "모든 플레이어에게 능력을 할당합니다.",
        color = CommonChatColor.AQUA,
        permission = AbilityPermission.ADMIN
    )
    fun ability_setAbilityAll(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        // 능력을 풀네임으로 먼저 검색해보고 없으면 능력 이름으로만 검색
        var targetAbility = AbilityApi.unsafe.abilityManager.getAbilityByFullName(result.enteredArguments)
        if (targetAbility == null) {
            targetAbility = AbilityApi.unsafe.abilityManager.getAbility(result.subArgument(1))
        }

        if (targetAbility == null) {
            sender.sendWarning("존재하지 않는 능력입니다.")
            return
        }

        AbilityApi.unsafe.playerManager.getJoinedPlayers().forEach { targetGamePlayer ->
            if (targetGamePlayer.ability != null) {
                targetGamePlayer.resignAbility()
            }
            targetGamePlayer.assignAbility(targetAbility)
        }

        BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 §f모든 플레이어§b에게 능력을 할당했습니다.")
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
    }

    @Command(
        parent = ["능력자"],
        name = ["삭제", "resign", "removeAbility"],
        arguments = "<플레이어>",
        description = "플레이어의 능력을 삭제합니다.",
        color = CommonChatColor.AQUA,
        permission = AbilityPermission.ADMIN
    )
    fun ability_removeAbility(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!AbilityApi.game.isGameStarted) {
            sender.sendWarning("아직 게임이 시작되지 않았습니다.")
            return
        }

        val targetGamePlayer = AbilityApi.unsafe.playerManager.getByName(result.getArgument(0))
        if (targetGamePlayer == null) {
            sender.sendWarning("존재하지 않는 플레이어입니다.")
            return
        }
        if (!targetGamePlayer.isJoined) {
            sender.sendWarning("게임에 참여하지 않은 플레이어입니다.")
            return
        }
        if (targetGamePlayer.ability == null) {
            sender.sendWarning("능력이 없는 플레이어입니다.")
            return
        }

        targetGamePlayer.resignAbility()

        BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 §f${targetGamePlayer.displayName} §b님의 능력을 삭제했습니다.")
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
    }

    @Command(
        parent = ["능력자"],
        name = ["전체삭제", "resignAll", "removeAbilityAll"],
        description = "모든 플레이어의 능력을 삭제합니다.",
        color = CommonChatColor.AQUA,
        permission = AbilityPermission.ADMIN
    )
    fun ability_removeAbilityAll(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!AbilityApi.game.isGameStarted) {
            sender.sendWarning("아직 게임이 시작되지 않았습니다.")
            return
        }

        AbilityApi.unsafe.playerManager.getJoinedPlayers().forEach { targetGamePlayer ->
            if (targetGamePlayer.ability != null) {
                targetGamePlayer.resignAbility()
            }
        }

        BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 §f모든 플레이어§b의 능력을 삭제했습니다.")
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
    }

    @Command(
        parent = ["능력자"],
        name = ["쿨타임초기화", "resetCooldown", "resetCooltime"],
        arguments = "<플레이어>",
        description = "플레이어의 능력 쿨타임을 초기화합니다.",
        color = CommonChatColor.AQUA,
        permission = AbilityPermission.ADMIN
    )
    fun ability_resetCooldown(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!AbilityApi.game.isGameStarted) {
            sender.sendWarning("아직 게임이 시작되지 않았습니다.")
            return
        }

        val targetGamePlayer = AbilityApi.unsafe.playerManager.getByName(result.getArgument(0))
        if (targetGamePlayer == null) {
            sender.sendWarning("존재하지 않는 플레이어입니다.")
            return
        }
        if (!targetGamePlayer.isJoined) {
            sender.sendWarning("게임에 참여하지 않은 플레이어입니다.")
            return
        }
        if (targetGamePlayer.ability == null) {
            sender.sendWarning("능력이 없는 플레이어입니다.")
            return
        }
        if (targetGamePlayer.ability !is Cooldownable) {
            sender.sendWarning("쿨타임이 없는 능력을 가지고 있는 플레이어입니다.")
            return
        }

        if ((targetGamePlayer.ability as Cooldownable).cooldownTimer.isRunning) {
            (targetGamePlayer.ability as Cooldownable).cooldownTimer.cancel()
        }

        BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 §f${targetGamePlayer.displayName} §b님의 능력 쿨타임을 초기화했습니다.")
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
    }

    @Command(
        parent = ["능력자"],
        name = ["전체쿨타임초기화", "resetCooldownAll", "resetCooltimeAll"],
        description = "모든 플레이어의 능력 쿨타임을 초기화합니다.",
        color = CommonChatColor.AQUA,
        permission = AbilityPermission.ADMIN
    )
    fun ability_resetCooldownAll(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        if (!AbilityApi.game.isGameStarted) {
            sender.sendWarning("아직 게임이 시작되지 않았습니다.")
            return
        }

        AbilityApi.unsafe.playerManager.getJoinedPlayers().forEach { targetGamePlayer ->
            if (targetGamePlayer.ability != null
                && targetGamePlayer.ability is Cooldownable
                && (targetGamePlayer.ability as Cooldownable).cooldownTimer.isRunning) {
                (targetGamePlayer.ability as Cooldownable).cooldownTimer.cancel()
            }
        }

        BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 §f모든 플레이어§b의 능력 쿨타임을 초기화했습니다.")
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
    }

    @Command(
        parent = ["능력자"],
        name = ["관전강제해제", "spectatorOff", "offSpectator"],
        arguments = "(플레이어)",
        description = "관전 모드를 강제로 해제합니다.",
        color = CommonChatColor.AQUA,
        permission = AbilityPermission.ADMIN
    )
    fun ability_sepectatorOff(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        // 콘솔에서는 플레이어를 반드시 입력해야함
        if (result.argumentsLength < 1 && sender.isConsole) {
            result.sendUsage(sender)
            return
        }

        val targetGamePlayer = if (result.argumentsLength > 1) {
            AbilityApi.unsafe.playerManager.getByName(result.getArgument(0))
        } else {
            AbilityApi.unsafe.playerManager.get((sender as CommonPlayer).uuid)
        }

        if (targetGamePlayer == null) {
            sender.sendWarning("존재하지 않는 플레이어입니다.")
            return
        }
        if (!targetGamePlayer.isOnline) {
            sender.sendWarning("접속 중이 아닌 플레이어입니다.")
            return
        }
        if (!targetGamePlayer.isSpectator) {
            sender.sendWarning("이미 관전 중이 아닌 플레이어입니다.")
            return
        }

        BukkitAsyncScope.launch {
            GameCore.unsafe.playerService.disableSpectatorMode(targetGamePlayer)

            // 탈락 취소
            targetGamePlayer.isDefeated = false

            // 팀이 없으면 팀 생성
            if (targetGamePlayer.team == null) {
                val team = Team()
                targetGamePlayer.team = team
                team.addPlayer(targetGamePlayer)
                GameCore.unsafe.teamManager.add(team)
            }

            // 시작 아이템 지급
            targetGamePlayer.isReceiveStartItems = false
            targetGamePlayer.giveStartItem()

            // 능력이 없으면 새로 할당
            if (targetGamePlayer.ability == null) {
                BukkitAsyncScope.launch {
                    AbilityApi.unsafe.abilityService.drawAbility(targetGamePlayer)
                }
            }

            sender.sendMessage("§b관전 모드를 강제로 해제했습니다.")
        }
    }

}