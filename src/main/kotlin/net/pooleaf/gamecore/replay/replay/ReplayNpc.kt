package net.pooleaf.gamecore.replay.replay

import net.citizensnpcs.api.npc.NPC
import org.bukkit.Location

class ReplayNpc(val citizensNpc: NPC) {

    var isDefeated: Boolean = false

    var health: Double = 20.0

    val location: Location
        get() = citizensNpc.entity.location

    val datas = HashMap<String, Any>()

}