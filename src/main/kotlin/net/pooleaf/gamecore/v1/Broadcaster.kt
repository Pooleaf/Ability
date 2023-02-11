package net.pooleaf.gamecore.v1

import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.gui.bukkit.actionbar.ActionBar
import net.pooleaf.core.modules.gui.bukkit.title.DefaultTitleBuilder
import net.pooleaf.core.modules.gui.bukkit.title.Title
import org.bukkit.Bukkit
import org.bukkit.Sound

object Broadcaster {

    fun broadcast(message: Any?) {
        Bukkit.broadcastMessage(message as String?)
    }

    fun broadcastTitle(title: Title) {
        Bukkit.getOnlinePlayers().forEach { title.send(it) }
    }

    fun broadcastTitle(
        title: String,
        subtitle: String? = null,
        stayTick: Int? = null,
        fadeInTick: Int? = null,
        fadeOutTick: Int? = null
    ) {
        val titleBuilder = DefaultTitleBuilder()
            .title(title)
            .subtitle(subtitle)

        stayTick?.let {
            titleBuilder.stay(stayTick)
        }

        fadeInTick?.let {
            titleBuilder.fadeIn(fadeInTick)
        }

        fadeOutTick?.let {
            titleBuilder.fadeOut(fadeOutTick)
        }

        broadcastTitle(titleBuilder.build())
    }

    fun broadcastActionBarForever(message: String?) {
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            ActionBar.showForever(onlinePlayer, message)
        }
    }

    fun broadcastActionBar(message: String?) {
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            ActionBar.show(onlinePlayer, message)
        }
    }

    fun broadcastActionBar(message: String?, seconds: Int) {
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            ActionBar.show(onlinePlayer, message, seconds)
        }
    }

    fun broadcastWaitingActionBar(
        currentPlayingPlayerCount: Int = GameCore.playerManager.getOnlinePlayingPlayers().size,
        startPlayerCount: Int = GameCore.autoGameConfig.startPlayerCount
    ) {
        broadcastActionBarForever("§e다른 플레이어를 기다리는 중입니다. §f($currentPlayingPlayerCount/$startPlayerCount)")
    }

    fun removeActionBar() {
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            ActionBar.remove(onlinePlayer)
        }
    }

    fun broadcastSound(sound: Sound?, volume: Float, pitch: Float) {
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer.location, sound, volume, pitch)
        }
    }

    fun broadcastSound(sound: XSound, volume: Float, pitch: Float) {
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            sound.play(onlinePlayer!!, volume, pitch)
        }
    }

}