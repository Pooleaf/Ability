package net.pooleaf.ability.commands

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.GameCore
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class AbilityCommand {

    @Command(
        name = ["능력자", "ability", "va", "ha"],
        description = "능력자 명령어 목록을 확인합니다.",
        helpCommand = true
    )
    fun ability(sender: CommandSender, result: HelpCommandResult) {
    }

    @Command(
        parent = ["", "능력자"],
        name = ["능력확인", "확인", "smdfur", "ability", "help"],
        description = "보유 중인 능력을 확인합니다."
    )
    fun ability_help(player: Player, result: CommandResult) {
        val abilityPlayer = AbilityApi.playerManager.get(player.uniqueId) ?: error("gamePlayer cannot be null")
        if (abilityPlayer.ability == null) {
            player.sendMessage("§c능력을 보유하고 있지 않습니다.")
            return
        }

        abilityPlayer.ability!!.sendManual(player)
    }

    @Command(
        parent = ["", "능력자"],
        name = ["확정", "ghkrwjd", "yes"],
        description = "능력을 확정합니다."
    )
    fun ability_yes(player: Player, result: CommandResult) {
        val abilityPlayer = AbilityApi.playerManager.get(player.uniqueId) ?: error("gamePlayer cannot be null")

        val currentPhase = AbilityApi.game.phaseTask.phasePipeline.getCurrentPhase()
        if (!(currentPhase is AbilityDrawPhase)) {
            player.sendMessage("§c능력 추첨 중이 아닙니다.")
            return
        }

        if (abilityPlayer.tempAbility == null) {
            player.sendMessage("§c능력을 추첨된 능력이 없습니다.")
            return
        }

        if (abilityPlayer.abilityDrawComplete) {
            player.sendMessage("§c이미 능력을 확정했습니다.")
            return
        }

        abilityPlayer.abilityDrawComplete = true
        abilityPlayer.assignAbility(abilityPlayer.tempAbility!!.javaClass)
        abilityPlayer.tempAbility = null

        player.sendMessage("")
        player.sendMessage("${abilityPlayer.ability!!.name} §e능력을 확정했습니다.")
        XSound.ENTITY_PLAYER_LEVELUP.play(player, 1F, 1F)
    }

    @Command(
        parent = ["", "능력자"],
        name = ["다시뽑기", "etlQhqrl", "재추첨", "wocncja", "no"],
        description = "능력을 다시 뽑습니다."
    )
    fun ability_no(player: Player, result: CommandResult) {
        val abilityPlayer = AbilityApi.playerManager.get(player.uniqueId) ?: error("gamePlayer cannot be null")

        val currentPhase = AbilityApi.game.phaseTask.phasePipeline.getCurrentPhase()
        if (currentPhase !is AbilityDrawPhase) {
            player.sendMessage("§c능력 추첨 중이 아닙니다.")
            return
        }

        if (abilityPlayer.abilityDrawComplete) {
            player.sendMessage("§c이미 능력을 확정했습니다.")
            return
        }

        if (abilityPlayer.redrawCount >= abilityPlayer.maxRedrawCount) {
            player.sendMessage("§c더 이상 능력을 다시 뽑을 수 없습니다.")
            return
        }

        for (i in 1..20) {
            player.sendMessage("")
        }

        abilityPlayer.redrawCount++

        BukkitAsyncScope.launch {
            val tempAbility = AbilityApi.abilityManager.getRandomAbilityNoDuplicatedInTemp()
            AbilityApi.abilityDrawer.drawWithYesNoMessage(abilityPlayer, AbilityApi.abilityManager.abilities, tempAbility)
            abilityPlayer.ability?.let { ability ->
                player.sendMessage("")
                player.sendMessage("${ability.name} §e능력을 확정했습니다.")
                XSound.ENTITY_PLAYER_LEVELUP.play(player, 1F, 1F)
            }
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
        if (AbilityApi.game.gameStarted) {
            player.sendMessage("§c게임 중에는 스폰으로 이동할 수 없습니다.")
            return
        }

        TeleportUtil.teleport(player, spawnLocation)
        player.sendMessage("§e스폰으로 이동되었습니다.")
    }

}