package net.pooleaf.ability.pack.pooleaf.abilities

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.gui.bukkit.actionbar.showActionBar
import net.pooleaf.core.modules.support.bukkit.util.ItemBuilder
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerEditBookEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask

class DeathNote : Ability(), Listener {

    private var isUsed = false
    private var target: AbilityPlayer? = null
    private var locationTask: BukkitTask? = null
    private var deathJob: Job? = null

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "데스노트"
        rank = AbilityRank.HIDDEN
        type = AbilityType.PASSIVE
        description = listOf(
            "데스노트에 플레이어의 닉네임을 적으면 5분 뒤 해당 플레이어가 사망합니다.",
            "데스노트에 적힌 플레이어는 당신의 위치를 항상 알 수 있습니다.",
            "한 번만 사용할 수 있습니다.",
        )

        ban = false
    }

    override fun onAssign() {
        abilityPlayer?.player?.inventory?.addItem(deathNoteItem())
    }

    override fun onResign() {
        locationTask?.cancel()
        locationTask = null
        deathJob?.cancel()
        deathJob = null
        target = null
    }

    @EventHandler
    fun onEditBook(event: PlayerEditBookEvent) {
        val abilityPlayer = abilityPlayer ?: return
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isEnded || AbilityApi.game.isGodMode) return
        if (event.player != abilityPlayer.player) return
        val item = event.player.inventory.getItem(event.slot) ?: return
        if (!item.isSimilar(deathNoteItem())) return

        event.isCancelled = true

        if (isUsed) {
            abilityPlayer.sendWarningSafely("데스노트는 한 번만 사용할 수 있습니다.")
            return
        }

        val targetName = event.newBookMeta.pages
            .flatMap { it.lines() }
            .map { it.trim() }
            .firstOrNull { it.isNotBlank() }

        if (targetName == null) {
            abilityPlayer.sendWarningSafely("데스노트에 플레이어의 닉네임을 적어야 합니다.")
            return
        }

        val target = AbilityApi.unsafe.playerManager.getByName(targetName)
        if (target == null || !target.isPlaying()) {
            abilityPlayer.sendWarningSafely("생존 중인 플레이어의 닉네임을 적어야 합니다.")
            return
        }
        if (target == abilityPlayer) {
            abilityPlayer.sendWarningSafely("자신의 이름은 적을 수 없습니다.")
            return
        }

        useDeathNote(abilityPlayer, target)
    }

    private fun useDeathNote(abilityPlayer: AbilityPlayer, target: AbilityPlayer) {
        isUsed = true
        this.target = target
        abilityPlayer.player?.inventory?.removeItem(deathNoteItem())
        abilityPlayer.player?.updateInventory()

        abilityPlayer.sendMessageSafely("§f${target.displayName} §e님의 이름을 데스노트에 적었습니다.")
        target.sendMessageSafely("§c데스노트에 당신의 이름이 적혔습니다.")

        startLocationTracking(abilityPlayer, target)

        deathJob = BukkitAsyncScope.launch {
            delay(5 * 60 * 1000L)
            if (!AbilityApi.game.isGameStarted || AbilityApi.game.isEnded) return@launch
            if (this@DeathNote.target != target || !target.isPlaying()) return@launch

            target.defeat()
            locationTask?.cancel()
            locationTask = null
        }
    }

    private fun startLocationTracking(abilityPlayer: AbilityPlayer, target: AbilityPlayer) {
        locationTask?.cancel()
        locationTask = PooleafAbilityPlugin.instance.server.scheduler.runTaskTimer(PooleafAbilityPlugin.instance, {
            val player = abilityPlayer.player ?: return@runTaskTimer
            val targetPlayer = target.player ?: return@runTaskTimer
            if (!abilityPlayer.isPlaying() || !target.isPlaying()) {
                locationTask?.cancel()
                locationTask = null
                return@runTaskTimer
            }
            if (player.world != targetPlayer.world) {
                targetPlayer.showActionBar("§c데스노트 §7- §f${abilityPlayer.displayName} §e위치: §f다른 월드")
                return@runTaskTimer
            }

            val location = player.location
            targetPlayer.showActionBar("§c데스노트 §7- §f${abilityPlayer.displayName} §e위치: §f${location.blockX}, ${location.blockY}, ${location.blockZ}")
        }, 0L, 20L)
    }

    private fun deathNoteItem(): ItemStack {
        return ItemBuilder(Material.BOOK_AND_QUILL)
            .displayName("§0§l데스노트")
            .lore("§7첫 줄에 플레이어의 닉네임을 적으세요.")
            .build()
    }

}
