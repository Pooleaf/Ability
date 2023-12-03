package net.pooleaf.ability.commands

import com.cryptomorin.xseries.XMaterial
import com.cryptomorin.xseries.XSound
import net.pooleaf.ability.AbilityPermission
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.core.modules.annocommand.common.Command
import net.pooleaf.core.modules.annocommand.common.CommandResult
import net.pooleaf.core.modules.annocommand.common.HelpCommandResult
import net.pooleaf.core.modules.gui.bukkit.title.TitleBuilder
import net.pooleaf.core.modules.support.bukkit.messager.sendWarning
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import net.pooleaf.gamecore.GameCore
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.map.MapView.Scale
import org.bukkit.scoreboard.DisplaySlot
import kotlin.math.round

class AdminTestCommand: Listener {

    @Command(
        name = ["abtest"],
        helpCommand = true,
        permission = AbilityPermission.ADMIN
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

    @Command(
        parent = ["abtest"],
        name = ["currentMapItem"]
    )
    fun abtest_currentMapItem(player: Player, result: CommandResult) {
        val currentMap = GameCore.currentMap
        if (currentMap == null) {
            player.sendWarning("현재 맵이 없습니다.")
            return
        }

        val mapView = Bukkit.createMap(player.world)
        val mapId = mapView.id

        mapView.centerX = currentMap.centerX.toInt()
        mapView.centerZ = currentMap.centerZ.toInt()

        val scale = getScale((currentMap.worldBorderSize.toFloat() / 2).toInt())
        mapView.scale = scale

        val mapItem = ItemStack(Material.MAP, 1, mapId)
        val mapMeta = mapItem.itemMeta as MapMeta
        player.inventory.addItem(mapItem)
    }

    fun getScale(size: Int): Scale {
        return when {
            size <= 128 -> Scale.CLOSEST
            size <= 256 -> Scale.CLOSE
            size <= 512 -> Scale.NORMAL
            size <= 1024 -> Scale.FAR
            else -> Scale.FARTHEST
        }
    }

    fun getSize(scale: Scale): Int {
        return when (scale) {
            Scale.CLOSEST -> 128
            Scale.CLOSE -> 256
            Scale.NORMAL -> 512
            Scale.FAR -> 1024
            Scale.FARTHEST -> 2048
        }
    }

    @Command(
        parent = ["abtest"],
        name = ["health"],
        arguments = "<player>"
    )
    fun abtest_health(player: Player, result: CommandResult) {
        val targetPlayer = Bukkit.getPlayer(result.getArgument(0))
        val objective = targetPlayer.scoreboard.registerNewObjective("testh", "dummy")
        objective.displaySlot = DisplaySlot.BELOW_NAME
        objective.displayName = "ㅇㅇ"
        objective.getScore(player.name).score = round(player.health).toInt()
    }

}