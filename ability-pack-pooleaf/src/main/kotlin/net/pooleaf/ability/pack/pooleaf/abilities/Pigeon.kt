package net.pooleaf.ability.pack.pooleaf.abilities

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.gamecore.events.player.GamePlayerDefeatEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

class Pigeon : Ability(), Listener, CastByItemHandler, Cooldownable {

    private val touchedPlayerUuids = hashSetOf<UUID>()
    private var isWinProcessing = false

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "비둘기"
        rank = AbilityRank.HIDDEN
        type = AbilityType.ACTIVE
        description = listOf(
            "당신은 비둘기입니다.",
            "철괴로 플레이어를 좌클릭해 감염시킬 수 있습니다.",
            "모든 플레이어를 한 번씩 감염시키면 즉시 우승합니다.",
            "철괴 우클릭 시 아직 감염시키지 않은 플레이어 목록을 확인합니다.",
            "같은 팀 플레이어는 감염 대상에서 제외됩니다.",
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 0L)


    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        if (clickType != CastByItemHandler.ClickType.RIGHT) return false

        sendUntouchedPlayerList()
        return false
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onTouch(event: EntityDamageByEntityEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isEnded || AbilityApi.game.isGodMode) return

        val abilityPlayer = abilityPlayer ?: return
        val damager = event.damager as? Player ?: return
        if (damager != abilityPlayer.player) return
        if (damager.itemInHand?.type != Material.IRON_INGOT) return

        val targetPlayer = event.entity as? Player ?: return
        val targetAbilityPlayer = AbilityApi.unsafe.playerManager.get(targetPlayer.uniqueId) ?: return
        if (!isTouchTarget(abilityPlayer, targetAbilityPlayer)) return

        event.isCancelled = true

        if (touchedPlayerUuids.add(targetAbilityPlayer.uuid)) {
            abilityPlayer.sendMessageSafely("§f${targetAbilityPlayer.displayName} §e님을 감염시켰습니다.")
            abilityPlayer.playSoundSafely(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.35F, 1.4F)
        }

        checkWin()
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDefeat(event: GamePlayerDefeatEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isEnded || AbilityApi.game.isGodMode) return
        val abilityPlayer = abilityPlayer ?: return
        if (event.gamePlayer == abilityPlayer || isWinProcessing) return

        checkWin()
    }

    override fun onResign() {
        touchedPlayerUuids.clear()
        isWinProcessing = false
    }

    private fun sendUntouchedPlayerList() {
        val abilityPlayer = abilityPlayer ?: return
        val untouchedPlayers = getUntouchedPlayers()

        if (untouchedPlayers.isEmpty()) {
            abilityPlayer.sendMessageSafely("§a모두 감염시켰습니다.")
            return
        }

        val playerNames = untouchedPlayers.joinToString("§7, §f") { it.displayName }
        abilityPlayer.sendMessageSafely("§e아직 감염되지 않은 플레이어: §f${playerNames}")
    }

    private fun checkWin() {
        val abilityPlayer = abilityPlayer ?: return
        if (!abilityPlayer.isPlaying()) return
        if (isWinProcessing) return
        if (getUntouchedPlayers().isNotEmpty()) return

        isWinProcessing = true
        BukkitAsyncScope.launch {
            win()
        }
    }

    private suspend fun win() {
        BukkitBroadcaster.broadcast("§e비둘기가 모든 플레이어를 감염시켰습니다.")

        getTouchTargets().forEach { target ->
            if (target.isPlaying()) {
                target.defeat()
            }
        }
    }

    private fun getUntouchedPlayers(): List<AbilityPlayer> {
        return getTouchTargets()
            .filter { !touchedPlayerUuids.contains(it.uuid) }
    }

    private fun getTouchTargets(): List<AbilityPlayer> {
        val abilityPlayer = abilityPlayer ?: return emptyList()

        return AbilityApi.unsafe.playerManager.getPlayingPlayers()
            .filter { isTouchTarget(abilityPlayer, it) }
    }

    private fun isTouchTarget(abilityPlayer: AbilityPlayer, target: AbilityPlayer): Boolean {
        if (target == abilityPlayer) return false
        if (!target.isPlaying()) return false
        if (abilityPlayer.team != null && abilityPlayer.team == target.team) return false

        return true
    }

}
