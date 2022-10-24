package net.pooleaf.gamecore.map

import net.pooleaf.core.modules.annoconfig.AnnoConfigModule
import net.pooleaf.core.modules.support.common.logger.Logger
import net.pooleaf.core.modules.support.common.manager.AbstractManager
import net.pooleaf.gamecore.GameCore
import java.io.File
import java.util.concurrent.ConcurrentHashMap

abstract class GameMapManager<T: GameMap>: AbstractManager<String, T>() {

    val mapFolder: File by lazy {
        File(GameCore.gamePlugin.dataFolder, "map")
    }


    init {
        datas = ConcurrentHashMap()
    }


    /**
     * [T]를 생성하고 반환합니다.
     */
    abstract fun create(): T


    fun saveConfig(map: T) {
        mapFolder.mkdirs()
        AnnoConfigModule.save(File(mapFolder, map.name + ".yml"), map)
    }

    fun saveAllConfig() {
        values().forEach { saveConfig(it) }
    }

    fun deleteConfig(map: T) {
        val saveFile = File(mapFolder, map.name + ".yml")
        if (saveFile.exists()) {
            saveFile.delete()
        }
    }

    fun loadAllConfig() {
        clear()

        for (file in mapFolder.listFiles()) {
            if (!file.name.endsWith(".yml")) {
                continue
            }

            try {
                val map = create()
                AnnoConfigModule.load(file, map)
                set(map.name, map)
            } catch (exception: Exception) {
                Logger.warning(file.name + " 맵을 불러올 수 없습니다.")
                exception.printStackTrace()
            }
        }
    }


    /**
     * 랜덤 맵을 반환합니다.
     */
    fun getRandom(): T? {
        val maps: List<GameMap> = values()
            .filter{ it.canUse() }
            .toList()

        if (maps.isEmpty()) return null

        return maps[Math.random().toInt() * maps.size] as T
    }

}