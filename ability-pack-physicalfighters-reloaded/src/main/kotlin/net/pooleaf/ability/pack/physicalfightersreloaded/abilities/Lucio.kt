package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.ability.*
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.gamecore.GameCore
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Lucio : Ability(), CastByItemHandler, Cooldownable, Durationable {

    private var activeEffect: String? = null
    private val EFFECT_RANGE = 10.0

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

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT), ItemStack(Material.GOLD_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 45_000L)
    override val durationTimer: DurationTimer = object : DurationTimer(this, 10_000L) {
        override fun onStart() {
            super.onStart()
            applyTeamEffects()
        }

        override fun onEnd() {
            super.onEnd()
            removeTeamEffects()
            activeEffect = null
        }
    }

    override fun onCastByItem(
        playerInteractEvent: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        when (item.type) {
            Material.IRON_INGOT -> {
                activeEffect = "speed"
                abilityPlayer?.sendMessageSafely("§e속도 증가 모드를 활성화했습니다!")
            }
            Material.GOLD_INGOT -> {
                activeEffect = "regeneration"
                abilityPlayer?.sendMessageSafely("§e회복 증가 모드를 활성화했습니다!")
            }
            else -> return false
        }

        return true
    }

    private fun applyTeamEffects() {
        val abilityPlayer = abilityPlayer ?: return
        
        val nearbyAllies = GameCore.unsafe.playerManager.getOnlinePlayingPlayers()
            .filter { abilityPlayer.team == it.team }
            .filter { abilityPlayer.player.location.distance(it.player.player.location) <= EFFECT_RANGE }

        nearbyAllies.forEach { ally ->
            // 추가 체력 제공 (기본 효과)
            val currentHealth = ally.player.health
            val maxHealth = ally.player.maxHealth
            if (currentHealth < maxHealth) {
                ally.player.health = minOf(maxHealth, currentHealth + 4.0)
            }

            // 활성화된 효과에 따라 추가 효과 적용
            when (activeEffect) {
                "speed" -> {
                    // 이속 25% 증가 (SPEED 1 = 20% 증가, SPEED 2 = 40% 증가이므로 SPEED 1 사용)
                    ally.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 200, 0), true)
                }
                "regeneration" -> {
                    // 회복 속도 50% 증가 (REGENERATION 1 사용)
                    ally.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200, 0), true)
                }
            }
        }

        val effectName = when (activeEffect) {
            "speed" -> "속도 증가"
            "regeneration" -> "회복 증가"
            else -> "버프"
        }
        
        abilityPlayer.sendMessageSafely("§e${nearbyAllies.size}명의 아군에게 $effectName 효과를 적용했습니다!")
    }

    private fun removeTeamEffects() {
        val abilityPlayer = abilityPlayer ?: return
        
        val nearbyAllies = GameCore.unsafe.playerManager.getOnlinePlayingPlayers()
            .filter { abilityPlayer.team == it.team }
            .filter { abilityPlayer.player.location.distance(it.player.player.location) <= EFFECT_RANGE }

        nearbyAllies.forEach { ally ->
            // 포션 효과 제거
            ally.player.removePotionEffect(PotionEffectType.SPEED)
            ally.player.removePotionEffect(PotionEffectType.REGENERATION)
        }
    }
}