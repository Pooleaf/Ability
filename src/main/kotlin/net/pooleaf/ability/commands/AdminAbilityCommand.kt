package net.pooleaf.ability.commands

import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPermission
import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.commonsender.common.CommonCommandSender
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.gamecore.Broadcaster
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
        val currentPhase = AbilityApi.game.phaseTask.phasePipeline.getCurrentPhase()
        if (!(currentPhase is AbilityDrawPhase)) {
            sender.nmessage("§c능력 추첨 중이 아닙니다.")
            return
        }

        // 능력 추첨 페이즈 종료
        currentPhase.end()

        Broadcaster.broadcast("${sender.displayName} §b님께서 모든 플레이어의 능력을 강제로 확정시켰습니다.")
        Broadcaster.broadcastSound(XSound.ENTITY_ITEM_PICKUP, 1F, 1F)
    }


}