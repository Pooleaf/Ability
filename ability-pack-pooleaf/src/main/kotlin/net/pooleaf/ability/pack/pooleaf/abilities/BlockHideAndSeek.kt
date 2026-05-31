package net.pooleaf.ability.pack.pooleaf.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.pooleaf.PooleafAbilityPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask

class BlockHideAndSeek : Ability(), Listener, Cooldownable {

    private var isCamouflaged = false
    private var disguisedMaterial: Material? = null
    private var disguisedData: Byte = 0
    private var fakeBlockLocation: Location? = null
    private var previousHelmet: ItemStack? = null
    private var updateTask: BukkitTask? = null

    init {
        pluginName = PooleafAbilityPlugin.instance.name

        name = "블럭숨바꼭질"
        rank = AbilityRank.S
        type = AbilityType.ACTIVE
        description = listOf(
            "쉬프트를 누르는 동안 발 아래 블럭으로 변신합니다.",
            "쉬프트를 떼면 변신이 해제됩니다.",
            "변신 중에는 다른 플레이어에게 블럭처럼 보입니다.",
        )

        ban = false
    }

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 20_000L)


    override fun onResign() {
        stopCamouflage()
    }

    @EventHandler
    fun onToggleSneak(event: PlayerToggleSneakEvent) {
        val player = abilityPlayer?.player ?: return
        if (event.player != player) return

        if (event.isSneaking) {
            tryStartCamouflage(player)
        } else {
            stopCamouflage()
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = abilityPlayer?.player ?: return
        if (!isCamouflaged || event.player == player) return

        event.player.hidePlayer(player)
        fakeBlockLocation?.let { sendDisguiseBlock(event.player, it) }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = abilityPlayer?.player ?: return
        if (event.player == player) {
            stopCamouflage()
        }
    }

    @EventHandler
    fun onChangedWorld(event: PlayerChangedWorldEvent) {
        val player = abilityPlayer?.player ?: return
        if (event.player != player || !isCamouflaged) return

        restoreFakeBlock()
        hideFromEveryone()
        updateFakeBlock(force = true)
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = abilityPlayer?.player ?: return
        if (event.entity == player) {
            restoreDeathDrops(event)
            stopCamouflage()
        }
    }

    private fun tryStartCamouflage(player: Player) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isEnded || AbilityApi.game.isGodMode) return
        if (!canUse() || cooldownTimer.isRunning || isCamouflaged) return

        val block = player.location.block.getRelative(BlockFace.DOWN)
        if (!block.canDisguise()) {
            player.sendMessage("§c발 아래 블럭으로 위장할 수 없습니다.")
            return
        }

        startCamouflage(player, block)
        cooldownTimer.start()
    }

    private fun startCamouflage(player: Player, block: Block) {
        isCamouflaged = true
        disguisedMaterial = block.type
        disguisedData = block.data
        previousHelmet = player.inventory.helmet?.clone()
        player.inventory.helmet = ItemStack(block.type, 1, block.data.toShort())
        player.updateInventory()

        hideFromEveryone()
        updateFakeBlock(force = true)

        updateTask = Bukkit.getScheduler().runTaskTimer(PooleafAbilityPlugin.instance, {
            if (!isCamouflaged) return@runTaskTimer
            val currentPlayer = abilityPlayer?.player
            if (currentPlayer == null || !currentPlayer.isOnline || !currentPlayer.isSneaking) {
                stopCamouflage()
                return@runTaskTimer
            }

            updateFakeBlock()
        }, 1L, 2L)
    }

    private fun stopCamouflage() {
        if (!isCamouflaged) return

        val player = abilityPlayer?.player

        updateTask?.cancel()
        updateTask = null

        restoreFakeBlock()
        showToEveryone()

        if (player != null && player.isOnline) {
            player.inventory.helmet = previousHelmet
            player.updateInventory()
        }

        isCamouflaged = false
        disguisedMaterial = null
        disguisedData = 0
        fakeBlockLocation = null
        previousHelmet = null
    }

    private fun updateFakeBlock(force: Boolean = false) {
        val player = abilityPlayer?.player ?: return
        val nextLocation = player.location.block.location
        val currentLocation = fakeBlockLocation
        if (!force && currentLocation != null && currentLocation.isSameBlock(nextLocation)) return

        restoreFakeBlock()
        fakeBlockLocation = nextLocation

        Bukkit.getOnlinePlayers()
            .filter { it != player }
            .forEach { sendDisguiseBlock(it, nextLocation) }
    }

    private fun hideFromEveryone() {
        val player = abilityPlayer?.player ?: return
        Bukkit.getOnlinePlayers()
            .filter { it != player }
            .forEach { it.hidePlayer(player) }
    }

    private fun showToEveryone() {
        val player = abilityPlayer?.player ?: return
        Bukkit.getOnlinePlayers()
            .filter { it != player }
            .forEach { it.showPlayer(player) }
    }

    private fun sendDisguiseBlock(viewer: Player, location: Location) {
        val material = disguisedMaterial ?: return
        if (viewer.world != location.world) return

        viewer.sendBlockChange(location, material, disguisedData)
    }

    private fun restoreFakeBlock() {
        val location = fakeBlockLocation ?: return
        Bukkit.getOnlinePlayers()
            .filter { it.world == location.world }
            .forEach { it.sendActualBlock(location) }
    }

    private fun restoreDeathDrops(event: PlayerDeathEvent) {
        if (!isCamouflaged) return

        val fakeHelmet = event.entity.inventory.helmet?.clone()
        if (fakeHelmet != null) {
            event.drops.remove(fakeHelmet)
        }

        previousHelmet?.clone()?.let { event.drops.add(it) }
    }

    private fun Player.sendActualBlock(location: Location) {
        val block = location.block
        sendBlockChange(location, block.type, block.data)
    }

    private fun Location.isSameBlock(other: Location): Boolean {
        return world == other.world &&
            blockX == other.blockX &&
            blockY == other.blockY &&
            blockZ == other.blockZ
    }

    private fun Block.canDisguise(): Boolean {
        return type != Material.AIR && !isLiquid && type.isBlock
    }

}
