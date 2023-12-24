package net.pooleaf.ability.pack.physicalfightersreloaded.abilities

import net.pooleaf.ability.AbilityApi
import net.pooleaf.ability.ability.Ability
import net.pooleaf.ability.ability.AbilityRank
import net.pooleaf.ability.ability.AbilityType
import net.pooleaf.ability.pack.physicalfightersreloaded.PhysicalFightersReloadedPlugin
import net.pooleaf.ability.player.AbilityPlayer
import net.pooleaf.core.modules.support.bukkit.util.BukkitBroadcaster
import net.pooleaf.gamecore.events.player.GamePlayerDefeatEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class Mirroring : Ability(), Listener {

    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name

        name = "미러링"
        rank = AbilityRank.S
        type = AbilityType.PASSIVE
        description = listOf(
            "당신을 죽인 사람을 함께 저승으로 끌고갑니다.",
            "자신이 죽을경우 죽인 사람 역시 죽게됩니다.",
        )

        ban = false
    }


    @EventHandler
    fun onDeath(event: GamePlayerDefeatEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (player?.player != event.gamePlayer.player) return
        if (event.killerGamePlayer == null || !event.killerGamePlayer!!.isPlaying()) return

        BukkitBroadcaster.broadcast("§f${event.gamePlayer.displayName} §e님의 미러링 능력이 발동되었습니다.")

        // 이지스 사용 중엔 미러링 무효
        val killerAbilityPlayer = event.killerGamePlayer as AbilityPlayer
        val killerAbility = killerAbilityPlayer.ability
        if (killerAbility != null && killerAbility is Aegis && killerAbility.durationTimer.isRunning) {
            BukkitBroadcaster.broadcast("§e미러링 능력이 무효화 되었습니다.")
            return
        }

        // 미러링 발동이 후순위
        Bukkit.getScheduler().runTaskLater(PhysicalFightersReloadedPlugin.instance, {
            killerAbilityPlayer.player?.damage(5000.0, killerAbilityPlayer.player)
        }, 1L)
    }

}