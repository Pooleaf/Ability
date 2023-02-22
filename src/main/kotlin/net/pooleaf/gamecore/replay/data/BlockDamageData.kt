package net.pooleaf.gamecore.replay.data

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListeningWhitelist
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.events.PacketListener
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class BlockDamageData : PacketListener, RecordData, Listener {

    override val type: String = "blockDamage"

    lateinit var worldName: String
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var damage: Int = 0


    override fun play(replayPlayer: ReplayPlayer) {
        val viewer = replayPlayer.viewer

        val location = Location(Bukkit.getWorld(worldName), x.toDouble(), y.toDouble(), z.toDouble())
        // TODO
    }

    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return

        val packet = event.packet
        val position = packet.blockPositionModifier.read(0)
        var blockDamage = packet.integers.read(1)

        val recordData = BlockDamageData().apply {
            x = position.x
            y = position.y
            z = position.z
            damage = blockDamage
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

    override fun onPacketReceiving(event: PacketEvent?) {
    }

    override fun getSendingWhitelist(): ListeningWhitelist {
        return ListeningWhitelist.newBuilder()
            .types(PacketType.Play.Server.BLOCK_BREAK_ANIMATION)
            .build()
    }

    override fun getReceivingWhitelist(): ListeningWhitelist {
        return ListeningWhitelist.EMPTY_WHITELIST
    }

    override fun getPlugin(): Plugin {
        return GameCore.gamePlugin
    }

}