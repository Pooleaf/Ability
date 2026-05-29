package net.pooleaf.ability.ability

import com.cryptomorin.xseries.XSound
import kotlinx.coroutines.delay
import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.phases.AbilityDrawPhase
import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.core.modules.gui.bukkit.title.DefaultTitleBuilder
import net.pooleaf.core.modules.gui.bukkit.title.Title
import net.pooleaf.core.modules.gui.bukkit.title.TitleBuilder
import net.pooleaf.core.modules.support.bukkit.messager.sendMessageSafely
import net.pooleaf.core.modules.support.common.component.SimpleComponentBuilder
import net.pooleaf.gamecore.phases.GodModePhase

class AbilityService {

    /**
     * 능력을 사용할 수 있는 시점인지를 반환합니다.
     */
    fun canUseAbility(): Boolean {
        return AbilityApi.game.isGameStarted && !AbilityApi.game.isEnded && !AbilityApi.game.isGodMode
    }

    /**
     * 플레이어의 능력을 추첨합니다.
     * 만약 능력이 부족하다면 능력이 할당되지 않습니다.
     * [temp]: true일 경우 [abilityPlayer.tempAbility]에 할당되고 false일 경우 즉시 할당됩니다.
     * [abilities]: 룰렛에 표기될 능력
     * [pollingConstantly]: 일정한 폴링 딜레이 여부
     * [pollingCount]: 폴링 횟수
     * [startPollingPeriodMillis]: 폴링 시작 딜레이
     * [endPollingPeriodMillis]: [pollingConstantly]가 true일 경우 지속 시간, false일 경우 최대 가속 시간
     */
    suspend fun drawAbility(
        abilityPlayer: AbilityPlayer,
        abilities: List<Ability> = AbilityApi.unsafe.abilityManager.getDefaultDrawAbilities(),
        temp: Boolean = true,
        allowDuplicate: Boolean = AbilityApi.abilityGameConfig.allowAbilityDuplicate,
        pollingConstantly: Boolean = false,
        pollingCount: Int = 12,
        startPollingPeriodMillis: Long = 40L,
        endPollingPeriodMillis: Long = 240L
    ) {
        val ability = if (allowDuplicate) {
            abilities.randomOrNull()
        } else {
            val assignedAbilities = AbilityApi.unsafe.abilityManager.getAssignedAbilities().map { it.fullName }
            val tempAssignedAbilities = AbilityApi.unsafe.abilityManager.getTempAssignedAbilities().map { it.fullName }

            abilities.filter { !assignedAbilities.contains(it.fullName) }
                .filter { !tempAssignedAbilities.contains(it.fullName) }
                .randomOrNull()
        }

        // 능력 중복 방지를 위해 미리 임시 할당
        abilityPlayer.tempAbility = ability

        // 추첨 효과
        showDrawAbilityEffect(
            abilityPlayer,
            ability,
            abilities,
            pollingConstantly,
            pollingCount,
            startPollingPeriodMillis,
            endPollingPeriodMillis
        )

        // 능력 정보 메시지
        delay(2000L)

        ability?.sendManual(abilityPlayer.player) ?: abilityPlayer.sendWarningSafely("능력이 부족하여 능력을 할당받지 못했습니다.")
        abilityPlayer.playSoundSafely(XSound.ENTITY_ITEM_PICKUP, 0.4F, 1.0F)

        // 능력 추첨 페이즈일 때
        val currentPhase = AbilityApi.game.phasePipeline.currentPhase
        if (currentPhase is AbilityDrawPhase) {
            // 능력을 할당 받지 못했을 경우 능력 확정 처리
            if (abilityPlayer.tempAbility == null) {
                abilityPlayer.abilityDrawComplete = true
                sendDrawWaitActionBar(abilityPlayer)
            }
            // 재추첨 횟수가 남지 않았을 경우 능력 확정
            else if (abilityPlayer.redrawCount >= abilityPlayer.maxRedrawCount) {
                decideAbility(abilityPlayer)
            }
            // 확정, 재추첨 버튼
            else {
                abilityPlayer.sendMessageSafely("")
                delay(1000L)

                // 능력 강제 스킵 시 이미 assign 되어 null 반환
                if (abilityPlayer.tempAbility == null) return
                sendYesNoMessage(abilityPlayer)
                abilityPlayer.playSoundSafely( XSound.ENTITY_GENERIC_EAT, 0.5F, 1F)
            }
        }

        // 할당
        if (!temp && abilityPlayer.tempAbility != null) {
            abilityPlayer.assignAbility(abilityPlayer.tempAbility!!)
            abilityPlayer.tempAbility = null
        }
    }

    /**
     * 능력 추첨 효과를 보여줍니다.
     * [ability]: 확정 능력
     * [abilities]: 룰렛에 표기될 능력
     * [pollingConstantly]: 일정한 폴링 딜레이 여부
     * [pollingCount]: 폴링 횟수
     * [startPollingPeriodMillis]: 폴링 시작 딜레이
     * [endPollingPeriodMillis]: [pollingConstantly]가 true일 경우 지속 시간, false일 경우 최대 가속 시간
     */
    suspend fun showDrawAbilityEffect(
        abilityPlayer: AbilityPlayer,
        ability: Ability?,
        abilities: List<Ability>,
        pollingConstantly: Boolean = false,
        pollingCount: Int = 12,
        startPollingPeriodMillis: Long = 40L,
        endPollingPeriodMillis: Long = 240L
    ) {
        val delayMillis = (endPollingPeriodMillis - startPollingPeriodMillis).toFloat() / (pollingCount - 1)

        // 폴링 추첨 효과
        for (i in 1..pollingCount) {
            // 랜덤 능력 타이틀
            val pollingAbility = abilities.random()
            val title = createAbilityTitle(pollingAbility)

            abilityPlayer.sendTitleSafely(title)
            abilityPlayer.playSoundSafely(XSound.UI_BUTTON_CLICK, 0.4F, 1.0F)

            // 일정한 딜레이
            if (pollingConstantly) {
                delay(delayMillis.toLong())
            }
            // 가속 딜레이
            else {
                delay(startPollingPeriodMillis + (delayMillis * i).toLong())
            }
        }

        // 추첨 결정 효과
        val title = createAbilityTitle(ability, true)

        abilityPlayer.sendTitleSafely(title)
        when (ability?.rank) {
            AbilityRank.SS -> {
                abilityPlayer.playSoundSafely(XSound.ENTITY_IRON_GOLEM_DEATH, 0.6F, 1F)
                abilityPlayer.playSoundSafely(XSound.ENTITY_WITHER_SPAWN, 0.4F, 1F)
            }
            null -> abilityPlayer.playSoundSafely(XSound.ENTITY_VILLAGER_NO, 0.6F, 1.0F)
            else -> {
                abilityPlayer.playSoundSafely(XSound.ENTITY_PLAYER_LEVELUP, 0.4F, 0.5F)
            }
        }
    }

    /**
     * 플레이어에게 능력 확정, 재추첨 버튼 메시지를 보냅니다.
     */
    fun sendYesNoMessage(abilityPlayer: AbilityPlayer) {
        val ability = abilityPlayer.tempAbility ?: error("tempAbility cannot be null")

        // 확정 버튼
        val yesComponent = SimpleComponentBuilder("§2§l[ 능력 확정 ]")
            .clickRunCommand("/확정")
            .hoverShowText("§e클릭 시 §f${ability.name} ${ability.rank.color}(${ability.rank.name}) §e능력으로 확정합니다. §7(/확정)")
            .build()
        // 재추첨 버튼
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

        abilityPlayer.player?.sendMessageSafely(yesComponent)
    }

    /**
     * 능력 타이트를 생성하여 반환합니다.
     */
    private fun createAbilityTitle(ability: Ability?, bold: Boolean = false): Title {
        val boldText = if (bold) "§l" else ""
        val abilityName = ability?.let { "${ability.rank.color}${boldText}${ability.name}" } ?: "${boldText}?"
        val abilityRank = ability?.let { "${ability.rank.color}${boldText}${ability.rank.name} 등급" } ?: "${boldText}?"

        return TitleBuilder()
            .title(abilityName)
            .subtitle(abilityRank)
            .stay(1 * 20)
            .fadeOut(1 * 20)
            .build()
    }

    /**
     * 능력을 확정합니다.
     */
    fun decideAbility(abilityPlayer: AbilityPlayer) {
        if (abilityPlayer.abilityDrawComplete) error("abilityPlayer already completed draw")

        val ability = abilityPlayer.tempAbility ?: error("tempAbility cannot be null")

        // 능력 확정
        abilityPlayer.abilityDrawComplete = true
        abilityPlayer.assignAbility(ability)
        abilityPlayer.tempAbility = null

        abilityPlayer.sendMessageSafely("")
        abilityPlayer.sendMessageSafely("${ability.rank.color}${ability.name} §e능력을 확정했습니다.")
        abilityPlayer.sendMessageSafely("/능력 §e명령어를 사용하여 능력을 다시 확인할 수 있습니다.")

        abilityPlayer.sendTitleSafely(
            DefaultTitleBuilder()
                .title("${ability.rank.color}${abilityPlayer.ability?.name}")
                .subtitle("§f능력을 확정했습니다.")
                .build()
        )
        abilityPlayer.playSoundSafely(XSound.ENTITY_PLAYER_LEVELUP, 0.4F, 1.0F)

        // 다른 플레이어 대기 액션바
        sendDrawWaitActionBar(abilityPlayer)
    }

    /**
     * 능력을 재추첨합니다.
     */
    suspend fun redrawAbility(
        abilityPlayer: AbilityPlayer,
        abilities: List<Ability> = AbilityApi.unsafe.abilityManager.getDefaultDrawAbilities(),
        allowDuplicate: Boolean = AbilityApi.abilityGameConfig.allowAbilityDuplicate,
        pollingConstantly: Boolean = false,
        pollingCount: Int = 12,
        startPollingPeriodMillis: Long = 40L,
        endPollingPeriodMillis: Long = 240L
    ) {
        if (abilityPlayer.abilityDrawComplete) error("abilityPlayer already completed draw")
        if (abilityPlayer.redrawCount >= abilityPlayer.maxRedrawCount) error("abilityPlayer already drawn max redraw count")

        abilityPlayer.redrawCount++
        abilityPlayer.tempAbility = null

        // 재추첨
        drawAbility(
            abilityPlayer,
            abilities,
            true,
            allowDuplicate,
            pollingConstantly,
            pollingCount,
            startPollingPeriodMillis,
            endPollingPeriodMillis
        )
    }

    /**
     * 능력 추첨 대기 액션바를 보냅니다.
     */
    private fun sendDrawWaitActionBar(abilityPlayer: AbilityPlayer) {
        val drawedPlayerCount = AbilityApi.unsafe.playerManager.getOnlinePlayingPlayers().count { it.abilityDrawComplete }
        val allPlayerCount = AbilityApi.unsafe.playerManager.getOnlinePlayingPlayers().size
        if (AbilityApi.unsafe.playerManager.getOnlinePlayingPlayers().any { !it.abilityDrawComplete }) {
            abilityPlayer.showActionBarForever("§e다른 플레이어를 기다리는 중입니다. §f(${drawedPlayerCount}/${allPlayerCount})")
        }
    }

    /**
     * 능력을 강제로 확정시킵니다.
     */
    fun skipAbilityDraw() {
        AbilityApi.unsafe.playerManager.getPlayingPlayers().filter { !it.abilityDrawComplete }
            .map { decideAbility(it) }
    }

    /**
     * 무적 시간을 건너뜁니다.
     */
    fun skipGodModePhase() {
        val currentPhase = AbilityApi.game.phasePipeline.currentPhase
        if (currentPhase !is GodModePhase) error("Current phase is not godModePhase")

        currentPhase.end()
    }

}
