package net.pooleaf.gamecore

import com.cryptomorin.xseries.XSound
import net.pooleaf.core.modules.gui.bukkit.actionbar.ActionBar
import net.pooleaf.core.modules.gui.bukkit.title.Title
import org.bukkit.Bukkit
import org.bukkit.Sound

class Broadcaster {

    companion object {

        fun broadcast(message: Any?) {
            Bukkit.broadcastMessage(message as String?)
        }

        fun broadcastTitle(title: Title) {
            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                title.send(onlinePlayer)
            }
        }

        fun broadcastTitle(title: String?) {
            broadcastTitle(
                DefaultTitleBuilder()
                    .title(title)
                    .build()
            )
        }

        fun broadcastTitle(title: String?, subtitle: String?) {
            broadcastTitle(
                DefaultTitleBuilder()
                    .title(title)
                    .subtitle(subtitle)
                    .build()
            )
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

}