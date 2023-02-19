package net.pooleaf.ability.ability

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.text.DecimalFormat

open class Ability() : Cloneable {

    // 플러그인 이름
    lateinit var pluginName: String
        protected set

    // 이름
    lateinit var name: String
        protected set

    // 플러그인 이름:이름
    val fullName
        get() = "${pluginName}:${name}"

    // 등급
    lateinit var rank: AbilityRank
        protected set

    // 패시브/액티브 타입
    lateinit var type: AbilityType
        protected set

    // 사용법
    lateinit var description: List<String>
        protected set

    // 할당 받은 플레이어
    var player: AbilityPlayer? = null

    // 능력 밴 여부
    open var ban: Boolean = false


    // 능력 초기화 여부
    val isInitialized
        get() = ::pluginName.isInitialized && ::name.isInitialized && ::rank.isInitialized && ::type.isInitialized && ::description.isInitialized

    /**
     * 능력 할당 시 호출됩니다.
     */
    protected open fun onAssign() {}

    /**
     * 능력 할당 해제 시 호출됩니다.
     */
    protected open fun onResign() {}

    /**
     * [player]에게 [Ability]를 할당합니다.
     */
    fun assign(player: AbilityPlayer) {
        // 이미 플레이어가 존재할 경우 할당 해제
        if (this.player != null) {
            resign()
        }

        this.player = player

        onAssign()

        if (this is Listener) {
            Bukkit.getPluginManager().registerEvents(this, AbilityPlugin.instance)
        }
    }

    /**
     * [player]에게서 [Ability] 할당을 해제합니다.
     */
    fun resign() {
        if (this.player == null) error("AbilityPlayer for resign ability '${fullName}' is null")

        onResign()

        // 쿨타임 종료
        if (this is Cooldownable && cooldownTimer.isRunning) {
            cooldownTimer.cancel()
        }

        // 지속시간 종료
        if (this is Durationable && durationTimer.isRunning) {
            durationTimer.cancel()
        }

        this.player = null

        if (this is Listener) {
            BukkitReflectionUtil.unregisterListener(this)
        }
    }

    fun sendManual(player: Player) {
        player?.sendMessage("§e§l====================================================")

        // 이름
        player?.sendMessage("")
        player?.sendMessage("§f§l${name}")

        // 등급
        player?.sendMessage("")
        player?.sendMessage("§e§l등급")
        player?.sendMessage("${rank.color}${rank.name}")

        // 설명
        player?.sendMessage("")
        player?.sendMessage("§b§l설명")
        description.forEach { player?.sendMessage(it) }

        // 쿨타임
        if (this is Cooldownable && cooldownMillis > 0) {
            val cooldown = DecimalFormat("#.##").format(cooldownMillis.toFloat() / 1000)
            player?.sendMessage("")
            player?.sendMessage("§a§l쿨타임")
            player?.sendMessage("§f${cooldown}§a초")
        }

        // 지속시간
        if (this is Durationable && durationMillis > 0) {
            val durationTime = DecimalFormat("#.##").format(durationMillis.toFloat() / 1000)
            player?.sendMessage("")
            player?.sendMessage("§a§l지속시간")
            player?.sendMessage("§f${durationTime}§a초")
        }

        player?.sendMessage("")
        player?.sendMessage("§e§l====================================================")
    }

    protected fun canUse(): Boolean {
        return AbilityApi.game.isGameStarted && !AbilityApi.game.isGodMode && !AbilityApi.game.isEnded
    }

    public override fun clone(): Ability {
        return super.clone() as Ability
    }

}