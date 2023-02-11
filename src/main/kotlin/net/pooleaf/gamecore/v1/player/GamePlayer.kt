package net.pooleaf.gamecore.v1.player

import com.google.common.base.Preconditions
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.core.modules.gui.GuiModule
import net.pooleaf.core.modules.support.bukkit.player.AbstractBukkitPlayer
import net.pooleaf.gamecore.v1.GameCore
import net.pooleaf.gamecore.v1.team.Team
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

open class GamePlayer : AbstractBukkitPlayer {

    // 게임 참여 여부
    var joined = false

    // 패배 여부
    var defeated = false

    // 관전 모드
    var observer = false

    // 팀
    var team: Team? = null


    internal constructor(uuid: UUID) : super(uuid)

    open suspend fun init() {
        joined = false
        defeated = false
        observer = false

        team?.removePlayer(this)

        BukkitSyncScope.launch {
            if (isOnline) {
                player.health = player.maxHealth
                player.level = 0
                player.exp = 0F
                player.inventory!!.clear()
                player.updateInventory()

                player.gameMode = GameCore.game.currentGameMode
                player.allowFlight = false
                player.isFlying = false
                player.spigot().collidesWithEntities = true
                player.activePotionEffects.forEach { player.removePotionEffect(it.type) }
                Bukkit.getOnlinePlayers().forEach { it.showPlayer(it) }

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

    /**
     * 관전 모드로 전환하거나 해제합니다.
     * Primary Thread에서 실행됩니다.
     */
    suspend fun toggleObserver(toggle: Boolean) {
        BukkitSyncScope.async {
            Preconditions.checkArgument(isOnline, "Player가 접속 중이 아닙니다.")

            player.health = player.maxHealth
            player.level = 0
            player.exp = 0F
            player.inventory!!.clear()
            player.updateInventory()

            if (toggle) {
                player.gameMode = GameMode.ADVENTURE
                player.allowFlight = true
                // 맵으로 이동했을 때만 날기 가능
                if (GameCore.game.isTeleportedToMap) {
                    player.isFlying = true
                }
                player.spigot().collidesWithEntities = false
                player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0, true))
                Bukkit.getOnlinePlayers().forEach { it.hidePlayer(it) }

                if (GameCore.game.isGameStarted) {
                    GameCore.quickBarManager.observerQuickBar.setTo(player)
                } else {
                    GameCore.quickBarManager.waitingQuickBar.setTo(player)
                }

                observer = true
            } else {
                player.gameMode = GameCore.game.currentGameMode

                player.allowFlight = false
                player.isFlying = false
                player.spigot().collidesWithEntities = true
                player.activePotionEffects.forEach { player.removePotionEffect(it.type) }
                Bukkit.getOnlinePlayers().forEach { it.showPlayer(it) }

                if (GameCore.game.isGameStarted) {
                    GuiModule.getQuickBarManager().removeTo(player)
                } else {
                    GameCore.quickBarManager.waitingQuickBar.setTo(player)
                }

                observer = false
            }
        }.await()
    }

}