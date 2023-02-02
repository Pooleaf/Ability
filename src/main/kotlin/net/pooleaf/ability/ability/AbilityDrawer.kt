package net.pooleaf.ability.ability

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.core.modules.coroutine.bukkit.BukkitAsyncScope
import net.pooleaf.core.modules.gui.bukkit.title.Title
import net.pooleaf.core.modules.gui.bukkit.title.TitleBuilder
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import org.bukkit.entity.Player

class AbilityDrawer(
    // 중복 허용
    var allowDuplicate: Boolean = true,
    var pollingCount: Int = 12,
    var startPollingPeriod: Long = 40L,
    var endPollingPeriod: Long = 240L
) {

    /**
     * [player]에게 능력 추첨 이팩트를 보여주고 추첨된 능력을 반환합니다.
     */
    suspend fun drawTo(
        player: Player,
        // 추첨할 능력
        abilities: List<Ability> = AbilityApi.abilityManager.abilities,
        // 추첨 결과 능력 미리 설정
        ability: Ability? = null
    ): Ability? {
        return BukkitAsyncScope.async {
            var tempAbility = abilities.random()

            // 추첨 타이틀 이팩트
            var pollingPeriod = startPollingPeriod
            val addPollingPeriod = (endPollingPeriod - startPollingPeriod).toFloat() / (pollingCount - 1)
            for (i in 0 until pollingCount) {
                tempAbility = abilities.random()

                createAbilityTitle(tempAbility).send(player)
                XSound.UI_BUTTON_CLICK.play(player)

                delay(pollingPeriod)
                pollingPeriod += addPollingPeriod.toInt()
            }

            // 미리 설정된 추첨 결과로 설정
            if (ability != null) {
                tempAbility = ability
            }
            // 중복 할당 허용 능력 추첨
            else if (allowDuplicate) {
                tempAbility = abilities.random()
            }
            // 중복 할당 비허용 능력 추첨
            else {
                val assignedAbilityNames = AbilityApi.abilityManager.getAssignedAbilities().map { it.fullName }
                tempAbility = abilities.filter { !assignedAbilityNames.contains(it.fullName) }.random()
            }

            // 할당 타이틀 이팩트
            createAbilityTitle(tempAbility, true).send(player)

            when (tempAbility.rank) {
                AbilityRank.SS -> {
                    XSound.ENTITY_IRON_GOLEM_DEATH.play(player)
                    XSound.ENTITY_WITHER_SPAWN.play(player, 0.7F, 1F)
                }
                else -> {
                    XSound.ENTITY_PLAYER_LEVELUP.play(player, 1F, 0.5F)
                }
            }

            return@async tempAbility
        }.await()
    }

    /**
     * [abilityPlayer]에게 능력 확정/재추첨 버튼 메시지를 보냅니다.
     */
    fun sendYesNoMessage(abilityPlayer: AbilityPlayer) {
        val ability = abilityPlayer.tempAbility ?: error("ability cannot be null")

        val yesComponent = SimpleComponentBuilder("§2§l[ 능력 확정 ]")
            .clickRunCommand("/확정")
            .hoverShowText("§e클릭 시 §f${ability.name} ${ability.rank.color}(${ability.rank.name}) §e능력으로 확정합니다. §7(/확정)")
            .build()
        val noComponent = if (abilityPlayer.redrawCount < abilityPlayer.maxRedrawCount) {
            SimpleComponentBuilder(" §c§l[ 다시 뽑기 ]")
                .clickRunCommand("/다시뽑기")
                .hoverShowText("§e클릭 시 능력을 다시 뽑습니다. (남은 기회: §f${abilityPlayer.maxRedrawCount - abilityPlayer.redrawCount}회§e) §7(/다시뽑기)")
                .build()
        } else {
            SimpleComponentBuilder(" §8§l[ 다시 뽑기 ]")
                .hoverShowText("§c더 이상 능력을 다시 뽑을 수 없습니다.")
                .build()
        }
        yesComponent.addExtra(noComponent)

        abilityPlayer.player?.sendMessage(yesComponent)
    }

    /**
     * 랜덤 능력을 뽑고 [abilityPlayer]에게 할당합니다.
     * 재추첨 기회가 있을 경우 [abilityPlayer.tempAbility]에 할당합니다.
     */
    suspend fun drawWithYesNoMessage(
        abilityPlayer: AbilityPlayer,
        // 추첨할 능력
        abilities: List<Ability> = AbilityApi.abilityManager.abilities,
        // 추첨 결과 능력 미리 설정
        ability: Ability? = null
    ): Ability? {
        return BukkitAsyncScope.async {
            // 능력 추첨
            val ability = AbilityApi.abilityDrawer.drawTo(abilityPlayer.player, abilities, ability)
            abilityPlayer.tempAbility = ability

            delay(2000L)

            abilityPlayer.player?.let { player ->
                // 능력 정보 메시지 보내기
                ability?.sendManual(player) ?: player.sendMessage("능력이 부족하여 능력을 할당받지 못했습니다.")
                XSound.ENTITY_ITEM_PICKUP.play(player)

                // 능력이 부족할 경우 능력 확정으로 취급
                if (ability == null) {
                    abilityPlayer.abilityDrawComplete = true
                    return@async null
                }

                // 능력 추첨 Phase일 경우 추가 메시지 보내기
                val currentPhase = AbilityApi.game.phaseTask.phasePipeline.getCurrentPhase()
                if (!(currentPhase is AbilityDrawPhase)) {
                    return@async ability
                }

                if (ability != null) {
                    player.sendMessage("")

                    delay(1000L)

                    // 재추첨 횟수가 남지 않았을 경우 능력 확정
                    if (abilityPlayer.redrawCount >= abilityPlayer.maxRedrawCount) {
                        abilityPlayer.abilityDrawComplete = true
                        abilityPlayer.assignAbility(abilityPlayer.tempAbility!!.javaClass)
                        abilityPlayer.tempAbility = null
                        return@async ability
                    }

                    // 능력 확정 버튼 메시지 보내기
                    AbilityApi.abilityDrawer.sendYesNoMessage(abilityPlayer)
                    XSound.ENTITY_GENERIC_EAT.play(player, 0.5F, 1F)
                }
            }

            return@async ability
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