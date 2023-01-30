package net.pooleaf.ability.commands

import com.cryptomorin.xseries.XMaterial
import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.gui.bukkit.title.TitleBuilder
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class TestCommand: Listener {

    @Command(
        name = ["abtest"],
        helpCommand = true,
        permission = "ability.admin"
    )
    fun abtest(sender: CommandSender, result: HelpCommandResult) {
    }

    @Command(
        parent = ["abtest"],
        name = ["drawEffect"],
        arguments = "(count) (period)"
    )
    fun abtest_drawEffect(player: Player, result: CommandResult) {
        val count = result.getArgumentAsInt(0) ?: 6
        val period = result.getArgumentAsLong(1) ?: 100L

        val abilityNames = listOf("데미갓", "강화술사", "능력탐지기", "라니", "가아라", "메딕", "미러링")

        Bukkit.getScheduler().runTaskAsynchronously(AbilityPlugin.instance) {
            for (i in 0..count) {
                TitleBuilder()
                    .title(abilityNames.random())
                    .stay(1 * 20)
                    .fadeOut(1 * 20)
                    .build()
                    .send(player)

                XSound.UI_BUTTON_CLICK.play(player)

                Thread.sleep(period)
            }

            TitleBuilder()
                .title("§5§l" + abilityNames.random())
                .stay(1 * 20)
                .fadeOut(1 * 20)
                .build()
                .send(player)
            XSound.ENTITY_IRON_GOLEM_DEATH.play(player)
            XSound.ENTITY_WITHER_SPAWN.play(player, 0.7F, 1F)
        }
    }

    val drawEffectBookItem = ItemBuilder(XMaterial.ENCHANTED_BOOK.parseMaterial())
        .displayName("§5S등급 확정 능력 재추첨권")
        .lore("§f우클릭 시 사용됩니다.")
        .build()

    @Command(
        parent = ["abtest"],
        name = ["drawEffectBook"]
    )
    fun abtest_drawEffectBook(player: Player, result: CommandResult) {
        player.inventory.addItem(drawEffectBookItem)
    }

    @EventHandler
    fun useDrawEffectBook(event: PlayerInteractEvent) {
        if (event.player.itemInHand.isSimilar(drawEffectBookItem)) {
            if (drawEffectBookItem.amount == 1) {
                event.player.inventory.removeItem(drawEffectBookItem)
            } else {
                drawEffectBookItem.amount -= 1
            }

            event.player.updateInventory()

            val count = 6
            val period = 100L

            val abilityNames = listOf("데미갓", "강화술사", "능력탐지기", "라니", "가아라", "메딕", "미러링")

            Bukkit.getScheduler().runTaskAsynchronously(AbilityPlugin.instance) {
                for (i in 0..count) {
                    TitleBuilder()
                        .title(abilityNames.random())
                        .stay(1 * 20)
                        .fadeOut(1 * 20)
                        .build()
                        .send(event.player)

                    XSound.UI_BUTTON_CLICK.play(event.player)

                    Thread.sleep(period)
                }

                TitleBuilder()
                    .title("§5§l" + abilityNames.random())
                    .stay(1 * 20)
                    .fadeOut(1 * 20)
                    .build()
                    .send(event.player)
                XSound.ENTITY_IRON_GOLEM_DEATH.play(event.player)
                XSound.ENTITY_WITHER_SPAWN.play(event.player, 0.7F, 1F)
            }
        }
    }

}