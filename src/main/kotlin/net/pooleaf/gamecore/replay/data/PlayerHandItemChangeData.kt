package net.pooleaf.gamecore.replay.data

import net.citizensnpcs.api.trait.trait.Equipment
import net.pooleaf.gamecore.replay.replay.ReplayPlayer
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.*

class PlayerHandItemChangeData : RecordData, Listener {

    override val type: String = "playerHandItemChange"

    lateinit var playerUuid: UUID
    lateinit var item: ItemStack

    override fun play(replayPlayer: ReplayPlayer) {
        val citizensNpc = replayPlayer.npcs.get(playerUuid)?.citizensNpc ?: return

        citizensNpc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.HAND, item)
    }

    // TODO record

}