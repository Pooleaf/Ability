package net.pooleaf.ability.compat.abilitywar

import Physical.Fighters.MainModule.AbilityBase
import Physical.Fighters.MainModule.EventManager
import kotlinx.coroutines.launch
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.ability.Cooldownable
import net.pooleaf.ability.ability.Durationable
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.ability.timer.DurationTimer
import net.pooleaf.ability.compat.CompatAbility
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import org.bukkit.event.Event

class AbilityWarCompatAbility: CompatAbility<AbilityBase>(), Cooldownable, Durationable {

    private lateinit var _cooldownTimer: CoolDownTimer
    private lateinit var _durationTimer: DurationTimer

    override val cooldownTimer: CoolDownTimer
        get() = _cooldownTimer
    override val durationTimer: DurationTimer
        get() = _durationTimer


    override fun convertFromOriginalAbility() {
        val abilityBase = originalAbility!!

        // 타입
        when (abilityBase.GetAbilityType()) {
            AbilityBase.Type.Active_Continue -> type = AbilityType.ACTIVE
            AbilityBase.Type.Active_Immediately -> type = AbilityType.ACTIVE
            else -> type = AbilityType.PASSIVE
        }

        // 랭크
        rank = when (abilityBase.GetRank()) {
            AbilityBase.Rank.GOD -> AbilityRank.SS
            AbilityBase.Rank.SSS -> AbilityRank.S
            AbilityBase.Rank.SS -> AbilityRank.S
            AbilityBase.Rank.S -> AbilityRank.A
            AbilityBase.Rank.A -> AbilityRank.B
            AbilityBase.Rank.B,
            AbilityBase.Rank.C,
            AbilityBase.Rank.F,
            AbilityBase.Rank.FF -> AbilityRank.C
        }

        // 정보
        pluginName = "PhysicalFighters"
        name = abilityBase.GetAbilityName()
        description = abilityBase.GetGuide().toList()

        // 시간
        _cooldownTimer = object : CoolDownTimer(this, abilityBase.GetCoolDown() * 1000L) {
            override fun onStart() {
                super.onStart()

                BukkitSyncScope.launch {
                    originalAbility?.A_CoolDownStart()
                }
            }

            override fun onEnd() {
                super.onEnd()

                BukkitSyncScope.launch {
                    originalAbility?.A_CoolDownEnd()
                }
            }
        }
        _durationTimer = object : DurationTimer(this, abilityBase.GetDuration() * 1000L) {
            override fun onStart() {
                super.onStart()

                BukkitSyncScope.launch {
                    originalAbility?.A_DurationStart()
                }
            }

            override fun onEnd() {
                super.onEnd()

                BukkitSyncScope.launch {
                    originalAbility?.A_DurationEnd()
                    originalAbility?.A_FinalDurationEnd()
                }
            }
        }
    }

    override fun onAssign() {
        BukkitSyncScope.launch {
            convertFromOriginalAbility()
            originalAbility?.SetPlayer(abilityPlayer?.player,  false)
        }
    }

    override fun onResign() {
        BukkitSyncScope.launch {
            originalAbility?.SetPlayer(null,  false)
        }
    }

    fun excute(event: Event, data: Int): Boolean {
        if (!canUse()) return false;

        // 무적 해제
        EventManager.DamageGuard = false

        val cd = originalAbility!!.A_Condition(event, data)
        if (cd == -2) return true
        if (cd == -1) return false

        if (cooldownTimer.isRunning || durationTimer.isRunning) return true

        // Active_Continue
        if (durationMillis > 0 && cooldownMillis > 0) {
            durationTimer.start()
        }
        // Active
        else if (cooldownMillis > 0) {
            originalAbility!!.A_Effect(event, cd)
            cooldownTimer.start()
        }
        // Passive
        else {
            originalAbility!!.A_Effect(event, cd)
        }

        return true
    }

}