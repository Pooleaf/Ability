package net.pooleaf.ability.ability.drawer

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.gui.bukkit.title.Title
import net.pooleaf.core.modules.gui.bukkit.title.TitleBuilder

class AbilityDrawer(
    // 추첨할 능력
    val abilities: List<Ability>,
    // 중복 허용
    val allowDuplicate: Boolean = true,
    val pollingCount: Int = 7,
    val pollingPeriod: Long = 100L
) {

    suspend fun drawTo(abilityPlayer: AbilityPlayer): Ability? {
        return BukkitAsyncScope.async {
            if (!AbilityApi.game.gameStarted) {
                cancel()
                return@async null
            }

            var ability = abilities.random()

            // 추첨 이펙트
            for (i in 0 until pollingCount) {
                ability = abilities.random()

                abilityPlayer.player?.let { player ->
                    createAbilityTitle(ability).send(player)
                    XSound.UI_BUTTON_CLICK.play(player)
                }

                delay(pollingPeriod)
            }

            // 중복 할당 허용
            if (allowDuplicate) {
                ability = abilities.random()
            }
            // 중복 할당 비허용
            else {
                val assignedAbilityNames = AbilityApi.abilityManager.getAssignedAbilities().map { it.fullName }
                ability = abilities.filter { !assignedAbilityNames.contains(it.fullName) }.random()
            }

            // 할당 이펙트
            abilityPlayer.player?.let { player ->
                createAbilityTitle(ability, true).send(player)

                when (ability.rank) {
                    AbilityRank.SS -> {
                        XSound.ENTITY_IRON_GOLEM_DEATH.play(player)
                        XSound.ENTITY_WITHER_SPAWN.play(player, 0.7F, 1F)
                    }
                    else -> {
                        XSound.ENTITY_PLAYER_LEVELUP.play(player, 1F, 0.5F)
                    }
                }
            }

            abilityPlayer.assignAbility(ability.javaClass)
            return@async abilityPlayer.ability
        }.await()
    }

    private fun createAbilityTitle(ability: Ability, bold: Boolean = false): Title {
        val boldText = if (bold) "§l" else ""
        val abilityName = if (ability != null) "${ability.rank.color}${boldText}${ability.name}" else "${boldText}?"

        return TitleBuilder()
            .title(abilityName)
            .stay(1 * 20)
            .fadeOut(1 * 20)
            .build()
    }

}