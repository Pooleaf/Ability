package net.pooleaf.gamecore.replay.data.entity

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject
import com.comphenix.protocol.wrappers.WrappedWatchableObject
import net.minecraft.server.v1_8_R3.EntityItem
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack

/**
 * 아이템 Entity Index 10
 * https://wiki.vg/index.php?title=Entity_metadata&oldid=7415#Entity
 */
class ItemMetaDataData : RecordData {

    override val type: String = "itemMetaData"

    var entityId: Int = 0
    lateinit var value: ItemStack


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val location = replayPlayer.viewer.location
        val entityItem = EntityItem((location.world as CraftWorld).handle, location.x, location.y, location.z, CraftItemStack.asNMSCopy(value.clone()))

        val wrappedDataWatcher = WrappedDataWatcher(entityItem.dataWatcher)
        wrappedDataWatcher.setObject(10, value)

        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA)
        packet.integers.write(0, entityId + replayPlayer.entityIdOffset)
        packet.watchableCollectionModifier.write(0, wrappedDataWatcher.watchableObjects)
        ProtocolLibrary.getProtocolManager().sendServerPacket(replayPlayer.viewer, packet)
    }

}

class ItemMetaDataDataListener : PacketAdapter(GameCore.gamePlugin, PacketType.Play.Server.ENTITY_METADATA) {

    override fun onPacketSending(event: PacketEvent) {
        if (!GameCore.unsafe.recordManager.isRecording()) return
        if (!GameCore.unsafe.recordManager.isRecordingTargetPlayer(event.player)) return

        val packet = event.packet
        val entityId = packet.integers.read(0)
        val entity = packet.getEntityModifier(event.player.world).read(0)
        if (entity !is Item) return

        val entityMetaData = packet.watchableCollectionModifier.read(0)
        if (entityMetaData.isEmpty()) return

        val index = entityMetaData.get(0).index
        val value = entityMetaData.get(0).value

        if (index != 10) return

        val recordData = ItemMetaDataData().apply {
            this.entityId = entityId
            this.value = value as ItemStack
        }
        GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
    }

}