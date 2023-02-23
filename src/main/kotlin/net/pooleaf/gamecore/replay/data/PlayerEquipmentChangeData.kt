package net.pooleaf.gamecore.replay.data

import net.citizensnpcs.api.trait.trait.Equipment
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment
import net.pooleaf.core.modules.support.bukkit.util.BukkitReflectionUtil
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.replay.RecordStopEvent
import net.pooleaf.gamecore.events.replay.RecordTickEvent
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.*

class PlayerEquipmentChangeData : RecordData, Listener {

    override val type: String = "playerEquipmentChange"

    lateinit var playerUuid: UUID
    var equipmentType: Int = 0
    var item: ItemStack?= null


    override fun onPlay(replayPlayer: ReplayPlayer) {
        val citizensNpc = replayPlayer.npcs.get(playerUuid)?.citizensNpc ?: return

        val equipmentSlot = when(equipmentType) {
            0 -> Equipment.EquipmentSlot.HAND
            4 -> Equipment.EquipmentSlot.HELMET
            3 -> Equipment.EquipmentSlot.CHESTPLATE
            2 -> Equipment.EquipmentSlot.LEGGINGS
            1 -> Equipment.EquipmentSlot.BOOTS
            else -> return
        }

        citizensNpc.getOrAddTrait(Equipment::class.java).set(equipmentSlot, item)

        // 시티즌 API만 사용하면 즉시 반영이 안되므로 패킷으로 한번 더 보내줌
        val packet = PacketPlayOutEntityEquipment(citizensNpc.entity.entityId, equipmentType, CraftItemStack.asNMSCopy(item))
        BukkitReflectionUtil.sendPacket(replayPlayer.viewer, packet)
    }

}

class PlayerEquipmentChangeDataListener : Listener {

    val beforeHands = hashMapOf<Player, ItemStack>()
    val beforeHelmets = hashMapOf<Player, ItemStack>()
    val beforeChestplates = hashMapOf<Player, ItemStack>()
    val beforeLeggingses = hashMapOf<Player, ItemStack>()
    val beforeBootses = hashMapOf<Player, ItemStack>()


    @EventHandler
    fun onRecordTick(event: RecordTickEvent) {
        event.record.recordTargetPlayers.forEach { uuid ->
            val player = Bukkit.getPlayer(uuid)
            if (player == null) return@forEach

            val beforeHand = beforeHands.get(player)
            if (beforeHand != player.inventory.itemInHand) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 0
                    item = player.inventory.itemInHand
                }
                GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeHands.put(player, player.inventory.itemInHand)

            val beforeHelmet = beforeHelmets.get(player)
            if (beforeHelmet != player.inventory.helmet) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 4
                    item = player.inventory.helmet
                }
                GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeHelmets.put(player, player.inventory.helmet)

            val beforeChestplate = beforeChestplates.get(player)
            if (beforeChestplate != player.inventory.chestplate) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 3
                    item = player.inventory.chestplate
                }
                GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeChestplates.put(player, player.inventory.chestplate)

            val beforeLeggings = beforeLeggingses.get(player)
            if (beforeLeggings != player.inventory.leggings) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 2
                    item = player.inventory.leggings
                }
                GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeLeggingses.put(player, player.inventory.leggings)

            val beforeBoots = beforeBootses.get(player)
            if (beforeBoots != player.inventory.boots) {
                val recordData = PlayerEquipmentChangeData().apply {
                    playerUuid = player.uniqueId
                    equipmentType = 1
                    item = player.inventory.boots
                }
                GameCore.unsafe.recordManager.record!!.addRecordData(recordData)
            }
            beforeBootses.put(player, player.inventory.boots)
        }
    }

    @EventHandler
    fun onRecordStop(event: RecordStopEvent) {
        beforeHelmets.clear()
        beforeChestplates.clear()
        beforeLeggingses.clear()
        beforeBootses.clear()
    }

}