package net.pooleaf.gamecore.replay.replay.virtual.entity

import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.data.entity.*
import net.pooleaf.gamecore.replay.replay.virtual.VirtualHistory
import org.bukkit.entity.Player

data class VirtualEntity(
    val entityId: Int
) : VirtualHistory() {

    fun isSpawned(tick: Long): Boolean {
        val spawnEntityDataTick = histories.filter { it.value.filterIsInstance<SpawnEntityData>().isNotEmpty() }.firstNotNullOfOrNull { it.key }
        val entityDestroyDataTick = histories.filter { it.value.filterIsInstance<EntityDestroyData>().isNotEmpty() }.firstNotNullOfOrNull { it.key }

        if (spawnEntityDataTick == null && entityDestroyDataTick == null) return true
        if (spawnEntityDataTick != null && entityDestroyDataTick == null) return spawnEntityDataTick <= tick
        if (spawnEntityDataTick == null && entityDestroyDataTick != null) return tick < entityDestroyDataTick
        if (spawnEntityDataTick != null && entityDestroyDataTick != null) return spawnEntityDataTick <= tick && tick < entityDestroyDataTick

        return false
    }

    fun spawn(viewer: Player) {
        val spawnEntityData = histories.values.flatten()
            .filterIsInstance<SpawnEntityData>()
            .firstOrNull() ?: return
        val spawnEntityHandler = GameCore.unsafe.recordDataReplayHandlerManager.get(SpawnEntityData::class.java) ?: return

        spawnEntityHandler.onPlay(spawnEntityData, viewer)
    }

    fun destroy(viewer: Player) {
        var entityDestroyData = histories.values.flatten()
            .filterIsInstance<EntityDestroyData>()
            .firstOrNull()
        val entityDestroyHandler = GameCore.unsafe.recordDataReplayHandlerManager.get(EntityDestroyData::class.java) ?: return

        if (entityDestroyData == null) {
            entityDestroyData = EntityDestroyData()
            entityDestroyData.entityIds = arrayOf(entityId)

            entityDestroyHandler.onPlay(entityDestroyData, viewer)
        } else {
            entityDestroyHandler.onPlay(entityDestroyData, viewer)
        }
    }

    fun teleport(tick: Long, viewer: Player) {
        val entityTeleportData = histories.filterKeys { it <= tick }
            .filterValues { it.filterIsInstance<EntityTeleportData>().isNotEmpty() }
            .maxByOrNull { it.key }
            ?.value
            ?.filterIsInstance<EntityTeleportData>()
            ?.firstOrNull() ?: return
        val entityTeleportHandler = GameCore.unsafe.recordDataReplayHandlerManager.get(EntityTeleportData::class.java) ?: return

        entityTeleportHandler.onPlay(entityTeleportData, viewer)
    }

    fun timeMachine(beforeTick: Long, newTick: Long, viewer: Player) {
        val beforeSpawned = isSpawned(beforeTick)
        val newSpawned = isSpawned(newTick)

        // 스폰
        if (!beforeSpawned && newSpawned) {
            spawn(viewer)
            teleport(newTick, viewer)
        }
        // 디스폰
        else if (beforeSpawned && !newSpawned) {
            destroy(viewer)
        }

        val datas = arrayListOf<RecordData>()

        getCurrentData(CollectData::class.java, newTick)?.let { datas.addAll(it) }
        getCurrentData(EntityDestroyData::class.java, newTick)?.let { datas.addAll(it) }
        getLastData(EntityTeleportData::class.java, newTick)?.let { datas.add(it) }
        getLastData(EntityVelocityData::class.java, newTick)?.let { datas.add(it) }
        getLastData(ItemMetaDataData::class.java, newTick)?.let { datas.add(it) }

        datas.forEach { data ->
            val playerHandler = GameCore.unsafe.recordDataReplayHandlerManager.get(data.javaClass) ?: return
            playerHandler.onPlay(data, viewer)
        }
    }

}