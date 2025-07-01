package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.ability.*
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.gamecore.GameCore
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Lucio : Ability(), CastByItemHandler, Cooldownable, Durationable {

    private var buffType: BuffType = BuffType.NONE
    private val affectedPlayers = mutableSetOf<Player>()

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "루시우"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭시 주변 아군의 이속을 25% 증가시킵니다.",
            "금괴 클릭시 주변 아군의 회복 속도를 50% 증가시킵니다.",
            "능력 발동 범위 내 아군은 추가 체력을 획득합니다."
        )

        ban = false
    }

    override val castItem: List<ItemStack> = listOf(
        ItemStack(Material.IRON_INGOT),
        ItemStack(Material.GOLD_INGOT)
    )

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 45_000L)
    override val durationTimer: DurationTimer = object : DurationTimer(this, 10_000L) {
        override fun onEnd() {
            super.onEnd()
            removeEffectsFromAllies()
            buffType = BuffType.NONE
            affectedPlayers.clear()
        }
    }

    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        val abilityPlayer = abilityPlayer ?: return false

        when (item.type) {
            Material.IRON_INGOT -> {
                buffType = BuffType.SPEED
                abilityPlayer.sendMessageSafely("§e루시우 §7- §f이속 증가 §e모드가 활성화되었습니다!")
            }
            Material.GOLD_INGOT -> {
                buffType = BuffType.HEALING
                abilityPlayer.sendMessageSafely("§e루시우 §7- §a회복 속도 증가 §e모드가 활성화되었습니다!")
            }
            else -> return false
        }

        applyEffectsToAllies()
        return true
    }

    private fun applyEffectsToAllies() {
        val abilityPlayer = abilityPlayer ?: return
        val nearbyAllies = getNearbyAllies()

        affectedPlayers.clear()
        affectedPlayers.addAll(nearbyAllies.map { it.player })

        nearbyAllies.forEach { ally ->
            // 추가 체력 제공 (흡수 하트 2개 = 4 HP)
            ally.player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 10_000, 1), true)
            
            when (buffType) {
                BuffType.SPEED -> {
                    // 이속 25% 증가 (SPEED 레벨 0 = 20% 증가, 레벨 1 = 40% 증가이므로 레벨 0 사용)
                    ally.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 10_000, 0), true)
                    ally.sendMessageSafely("§f${abilityPlayer.displayName} §e님의 루시우로 §f이동속도§e가 증가했습니다!")
                }
                BuffType.HEALING -> {
                    // 회복 속도 50% 증가 (REGENERATION 레벨 1)
                    ally.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 10_000, 1), true)
                    ally.sendMessageSafely("§f${abilityPlayer.displayName} §e님의 루시우로 §a회복속도§e가 증가했습니다!")
                }
                BuffType.NONE -> return
            }
        }

        val buffName = when (buffType) {
            BuffType.SPEED -> "이속 증가"
            BuffType.HEALING -> "회복 속도 증가"
            BuffType.NONE -> ""
        }

        abilityPlayer.sendMessageSafely("§e주변 아군 §f${nearbyAllies.size}§e명에게 §f$buffName §e효과를 적용했습니다!")
    }

    private fun removeEffectsFromAllies() {
        affectedPlayers.forEach { player ->
            if (player.isOnline) {
                when (buffType) {
                    BuffType.SPEED -> {
                        player.removePotionEffect(PotionEffectType.SPEED)
                    }
                    BuffType.HEALING -> {
                        player.removePotionEffect(PotionEffectType.REGENERATION)
                    }
                    BuffType.NONE -> {}
                }
                // 흡수 효과는 자연스럽게 사라지도록 둠
            }
        }
    }

    private fun getNearbyAllies(): List<net.pooleaf.gamecore.player.GamePlayer> {
        val abilityPlayer = abilityPlayer ?: return emptyList()

        return GameCore.unsafe.playerManager.getOnlinePlayingPlayers()
            .filter { it != abilityPlayer } // 자신 제외
            .filter { abilityPlayer.team == it.team } // 같은 팀 (아군)
            .filter { abilityPlayer.player.location.distance(it.player.location) <= 8.0 } // 8블록 반경
    }

    private enum class BuffType {
        NONE,
        SPEED,
        HEALING
    }
}