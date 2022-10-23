package net.pooleaf.gamecore.player

import com.google.common.base.Preconditions
import net.pooleaf.core.modules.gui.GuiModule
import net.pooleaf.core.modules.gui.bukkit.title.Title
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.modules.support.common.player.AbstractPlayer
import net.pooleaf.gamecore.DefaultTitleBuilder
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.quickbars.ObserverQuickBar
import net.pooleaf.gamecore.team.Team
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

open class GamePlayer(uuid: UUID, var team: Team) : AbstractPlayer<Player>(uuid) {

    var joined = false // 게임 참여 여부
    var defeated = false // 패배 여부
    var observer = false // 관전 모드


    open fun init() {
        joined = false
        defeated = false
        observer = false

        Bukkit.getScheduler().runTask(GameCore.gamePlugin) {
            if (player != null) {
                player.health = player.maxHealth
                player.level = 0
                player.exp = 0F
                player.inventory!!.clear()
                player.updateInventory()

                player.allowFlight = false
                player.isFlying = false
                player.spigot().collidesWithEntities = true
                player.activePotionEffects.forEach { player.removePotionEffect(it.type) }
                // TODO hide player 해제

                GuiModule.getQuickBarManager().removeTo(player)
            }
        }
    }


    /**
     * 게임을 참가하고 탈락하지 않고 관전 중이 아닌지 확인합니다.
     */
    fun isPlaying(): Boolean {
        return joined && !defeated && !observer
    }

    fun sendTitle(title: Title) {
        title.send(player)
    }

    fun sendTitle(title: String?) {
        sendTitle(
            DefaultTitleBuilder()
                .title(title)
                .build()
        )
    }

    fun sendTitle(title: String?, subtitle: String?) {
        sendTitle(
            DefaultTitleBuilder()
                .title(title)
                .subtitle(subtitle)
                .build()
        )
    }

    fun toggleObserver(toggle: Boolean) {
        Preconditions.checkArgument(player.isOnline, "Player가 접속 중이 아닙니다.")

        Bukkit.getScheduler().runTask(GameCore.gamePlugin) {
            player.health = player.maxHealth
            player.level = 0
            player.exp = 0F
            player.inventory!!.clear()
            player.updateInventory()
        }

        if (toggle) {
            Bukkit.getScheduler().runTask(GameCore.gamePlugin) {
                player.gameMode = GameMode.ADVENTURE
                player.allowFlight = true
                player.isFlying = true
                player.spigot().collidesWithEntities = false
                player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0, true))
                // TODO hide player

                ObserverQuickBar().setTo(player)
            }

            observer = true
        } else {
            Bukkit.getScheduler().runTask(GameCore.gamePlugin) {
                if (GameCore.game.gameStarted) {
                    player.gameMode = GameMode.SURVIVAL
                } else {
                    player.gameMode = GameMode.ADVENTURE
                }

                player.allowFlight = false
                player.isFlying = false
                player.spigot().collidesWithEntities = true
                PotionEffectType.values().forEach { player.removePotionEffect(it) }
                // TODO hide player 해제

                GuiModule.getQuickBarManager().removeTo(player)
            }

            observer = false
        }
    }

}