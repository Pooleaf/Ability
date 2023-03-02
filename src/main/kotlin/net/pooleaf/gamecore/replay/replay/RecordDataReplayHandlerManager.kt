package net.pooleaf.gamecore.replay.replay

import com.comphenix.protocol.ProtocolLibrary
import net.pooleaf.core.modules.support.common.manager.AbstractManager
import net.pooleaf.gamecore.replay.data.RecordData
import net.pooleaf.gamecore.replay.data.TpsData
import net.pooleaf.gamecore.replay.data.TpsDataReplayHandler
import net.pooleaf.gamecore.replay.data.block.*
import net.pooleaf.gamecore.replay.data.entity.*
import net.pooleaf.gamecore.replay.data.game.*
import net.pooleaf.gamecore.replay.data.player.*
import net.pooleaf.gamecore.replay.listeners.VirtualChunkLoadListener

class RecordDataReplayHandlerManager : AbstractManager<Class<out RecordData>, RecordDataReplayHandler<out RecordData>>() {

    fun registerHandlers() {
        // Block
        set(BlockBreakData::class.java, BlockBreakDataReplayHandler())
        set(BlockChangeData::class.java, BlockChangeDataReplayHandler())
        set(BlockDamageData::class.java, BlockDamageDataReplayHandler())
        set(EntityChangeBlockData::class.java, EntityChangeBlockDataReplayHandler())
        set(EntityExplodeData::class.java, EntityExplodeDataReplayHandler())
        set(BlockPlaceData::class.java, BlockPlaceDataReplayHandler())
        set(MultiBlockChangeData::class.java, MultiBlockChangeDataReplayHandler())
        set(UpdateSignData::class.java, UpdateSignDataReplayHandler())

        // Entity
        set(CollectData::class.java, CollectDataReplayHandler())
        set(EntityDestroyData::class.java, EntityDestroyDataReplayHandler())
        set(EntityTeleportData::class.java, EntityTeleportDataReplayHandler())
        set(EntityVelocityData::class.java, EntityVelocityDataReplayHandler())
        set(ItemDespawnData::class.java, ItemDespawnDataReplayHandler())
        set(ItemMetaDataData::class.java, ItemMetaDataDataReplayHandler())
        set(SpawnEntityData::class.java, SpawnEntityDataReplayHandler())

        // Player
        set(PlayerAnimationData::class.java, PlayerAnimationDataReplayHandler())
        set(PlayerChatData::class.java, PlayerChatDataReplayHandler())
        set(PlayerDamageData::class.java, PlayerDamageDataReplayHandler())
        set(PlayerEquipmentChangeData::class.java, PlayerEquipmentChangeDataReplayHandler())
        set(PlayerHealthChangeData::class.java, PlayerHealthChangeDataReplayHandler())
        set(PlayerMetaDataData::class.java, PlayerMetaDataDataReplayHandler())
        set(PlayerMoveData::class.java, PlayerMoveDataReplayHandler())
        set(PlayerTeleportData::class.java, PlayerTeleportDataReplayHandler())

        // Game
        set(GameEndData::class.java, GameEndDataReplayHandler())
        set(GamePlayerDefeatData::class.java, GamePlayerDefeatDataReplayHandler())
        set(GameWorldBorderChangeData::class.java, GameWorldBorderChangeDataReplayHandler())
        set(TeamDefeatData::class.java, TeamDefeatDataReplayHandler())

        // ETC
        set(TpsData::class.java, TpsDataReplayHandler())

        // Chunk
        ProtocolLibrary.getProtocolManager().addPacketListener(VirtualChunkLoadListener())
    }

}