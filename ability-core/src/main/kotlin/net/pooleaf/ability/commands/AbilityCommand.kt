package net.pooleaf.ability.commands

import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPermission
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import net.pooleaf.core.modules.support.common.pageable.PageableCommand
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.commands.GameCommand
import net.pooleaf.gamecore.kit.Kit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AbilityCommand {

    private val gameCommand = GameCommand()

    @Command(
        name = ["능력자", "ability", "va", "ha", "ua"],
        description = "능력자 명령어 목록을 확인합니다.",
        helpCommand = true
    )
    fun ability(sender: CommandSender, result: HelpCommandResult) {
    }

    @Command(
        parent = ["", "능력자"],
        name = ["능력", "smdfur", "ability", "help"],
        description = "보유 중인 능력을 확인합니다."
    )
    fun ability_help(player: Player, result: CommandResult) {
        val abilityPlayer = AbilityApi.unsafe.playerManager.get(player.uniqueId) ?: error("gamePlayer cannot be null")
        val ability = abilityPlayer.ability ?: abilityPlayer.tempAbility
        if (ability == null) {
            player.sendMessage("§c능력을 보유하고 있지 않습니다.")
            return
        }

        player.sendMessage("")
        ability!!.sendManual(player)
    }

    @Command(
        parent = ["", "능력자"],
        name = ["확정", "ghkrwjd", "yes"],
        description = "능력을 확정합니다."
    )
    fun ability_yes(player: Player, result: CommandResult) {
        val abilityPlayer = AbilityApi.unsafe.playerManager.get(player.uniqueId) ?: error("gamePlayer cannot be null")

        val currentPhase = AbilityApi.game.phasePipeline.currentPhase
        if (!(currentPhase is AbilityDrawPhase)) {
            player.sendMessage("§c능력 추첨 중이 아닙니다.")
            return
        }

        if (abilityPlayer.abilityDrawComplete) {
            player.sendMessage("§c이미 능력을 확정했습니다.")
            return
        }

        if (abilityPlayer.tempAbility == null) {
            player.sendMessage("§c추첨된 능력이 없습니다.")
            return
        }

        if (abilityPlayer.abilityDrawComplete) {
            player.sendMessage("§c이미 능력을 확정했습니다.")
            return
        }

        if (abilityPlayer.abilityDrawJob?.let { it.isActive } == true) {
            player.sendWarning("능력 추첨이 끝날 때까지 기다려주세요.")
            return
        }

        AbilityApi.unsafe.abilityService.decideAbility(abilityPlayer)
    }

    @Command(
        parent = ["", "능력자"],
        name = ["다시뽑기", "etlQhqrl", "재추첨", "wocncja", "no"],
        description = "능력을 다시 뽑습니다."
    )
    fun ability_no(player: Player, result: CommandResult) {
        val abilityPlayer = AbilityApi.unsafe.playerManager.get(player.uniqueId) ?: error("gamePlayer cannot be null")

        val currentPhase = AbilityApi.game.phasePipeline.currentPhase
        if (currentPhase !is AbilityDrawPhase) {
            player.sendMessage("§c능력 추첨 중이 아닙니다.")
            return
        }

        if (abilityPlayer.abilityDrawComplete) {
            player.sendMessage("§c이미 능력을 확정했습니다.")
            return
        }

        if (abilityPlayer.tempAbility == null) {
            player.sendMessage("§c추첨된 능력이 없습니다.")
            return
        }

        if (abilityPlayer.redrawCount >= abilityPlayer.maxRedrawCount) {
            player.sendMessage("§c더 이상 능력을 다시 뽑을 수 없습니다.")
            return
        }

        if (abilityPlayer.abilityDrawJob?.let { it.isActive } == true) {
            player.sendWarning("능력 추첨이 끝날 때까지 기다려주세요.")
            return
        }

        for (i in 1..20) {
            player.sendMessage("")
        }

        abilityPlayer.abilityDrawJob = BukkitAsyncScope.launch {
            AbilityApi.unsafe.abilityService.redrawAbility(abilityPlayer)
        }
    }

    @Command(
        parent = ["", "능력자"],
        name = ["스폰", "spawn", "넴주"],
        description = "스폰으로 이동합니다."
    )
    fun ability_spawn(player: Player, result: CommandResult) {
        val spawnLocation = GameCore.spawnConfig.spawnLocation

        // 스폰이 설정 되어있는지 확인
        if (spawnLocation == null) {
            player.sendMessage("§c스폰이 설정되지 않았습니다.")
            return
        }

        // 게임 중에는 스폰으로 이동 불가
        if (AbilityApi.game.isTeleportedToMap || AbilityApi.game.isGameStarted) {
            player.sendMessage("§c게임 중에는 스폰으로 이동할 수 없습니다.")
            return
        }

        TeleportUtil.teleport(player, spawnLocation)
        player.sendMessage("§e스폰으로 이동되었습니다.")
    }

    @Command(
        parent = ["능력자"],
        name = ["능력목록", "abilityList", "listAbility"],
        arguments = "(페이지)",
        description = "능력 목록을 확인합니다."
    )
    fun ability_abilityList(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        val abilities = AbilityApi.unsafe.abilityManager.abilities.sortedBy { it.fullName }

        object : PageableCommand<Ability>(result.entered, abilities, 7) {
            override fun getHeaderColor(): CommonChatColor {
                return CommonChatColor.RED
            }

            override fun getHeaderMessage(): String {
                return "능력 목록"
            }

            override fun handleValue(ability: Ability, i: Int): Any {
                return SimpleComponentBuilder("${headerColor}[ ${i + 1} ] §f${ability.fullName}")
                    .hoverShowText("${headerColor}클릭 시 §f${ability.fullName} ${headerColor}능력 정보를 확인합니다.")
                    .clickRunCommand("/능력자 능력정보 ${ability.fullName}")
                    .build()
            }
        }.sendPage(sender, result.getArgumentAsInt(0))
    }

    @Command(
        parent = ["능력자"],
        name = ["능력정보", "abilityInfo", "infoAbility"],
        arguments = "<능력이름|플러그인이름:능력이름>",
        description = "능력 정보를 확인합니다."
    )
    fun ability_abilityInfo(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        // 능력을 풀네임으로 먼저 검색해보고 없으면 능력 이름으로만 검색
        var targetAbility = AbilityApi.unsafe.abilityManager.getAbilityByFullName(result.enteredArguments)
        if (targetAbility == null) {
            targetAbility = AbilityApi.unsafe.abilityManager.getAbility(result.enteredArguments)
        }

        if (targetAbility == null) {
            sender.sendWarning("존재하지 않는 능력입니다.")
            return
        }

        targetAbility.sendManual(sender.platformSender)
    }

    @Command(
        parent = ["능력자"],
        name = ["플레이어목록", "목록", "playerList", "list"],
        description = "플레이어 목록을 확인합니다."
    )
    fun ability_playerList(sender: CommonCommandSender<CommandSender>, result: CommandResult) {
        gameCommand.game_playerList(sender, result)
    }

}