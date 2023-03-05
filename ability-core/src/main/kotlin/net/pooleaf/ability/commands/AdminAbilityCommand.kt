package net.pooleaf.ability.commands

import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPermission
import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.core.modules.support.common.CommonChatColor
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
            sender.sendMessage("§c능력 추첨 중이 아닙니다.")
            return
        }

        if (!AbilityApi.unsafe.playerManager.getPlayingPlayers().any { it.tempAbility != null }) {
            sender.sendMessage("§c능력 추첨 중이 아닙니다.")
            return
        }

        AbilityApi.unsafe.abilityService.skipAbilityDraw()

        BukkitBroadcaster.broadcast("${sender.displayName} §b님께서 모든 플레이어의 능력을 강제로 확정시켰습니다.")
        BukkitBroadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 0.4F, 0.4F)
    }


}