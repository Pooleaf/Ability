package net.pooleaf.gamecore.map

import com.google.common.base.Preconditions
import com.grinderwolf.swm.api.SlimePlugin
import com.grinderwolf.swm.api.world.properties.SlimeProperties
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap
import net.pooleaf.ability.AbilityPlugin
import net.pooleaf.core.modules.annoconfig.AnnoConfigModule
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigExclude
import net.pooleaf.core.modules.annoconfig.common.anno.ConfigName
import net.pooleaf.core.modules.support.bukkit.util.TeleportUtil
import net.pooleaf.gamecore.GameCore
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class GameMap {

    @ConfigName("이름")
    var name: String? = null

    @ConfigName("중앙 위치.world")
    var centerWorldName: String? = null

    @ConfigName("중앙 위치.x")
    var centerX = 0.0

    @ConfigName("중앙 위치.y")
    var centerY = 0.0

    @ConfigName("중앙 위치.z")
    var centerZ = 0.0

    @ConfigName("중앙 위치.yaw")
    var centerYaw = 0f

    @ConfigName("중앙 위치.pitch")
    var centerPitch = 0f

    @ConfigName("경계선 범위")
    val worldBorderSize: Int? = null // 맵 범위


    @ConfigExclude
    var currentWorldBorderSize: Int? = null // 현재 맵 범위 (경계선 줄일 때 사용)


    /**
     * 현재 경계선 설정을 초기화합니다.
     */
    fun initWorldBorderSettings() {
        currentWorldBorderSize = worldBorderSize
    }

    /**
     * 사용 가능한 맵인지 반환합니다.
     */
    fun canUse(): Boolean {
        return centerWorldName != null && worldBorderSize != null
    }

    /**
     * 중앙 위치를 설정합니다.
     */
    fun setCenterLocation(location: Location) {
        centerWorldName = location.world.name
        centerX = location.x
        centerY = location.y
        centerZ = location.z
        centerYaw = location.yaw
        centerPitch = location.pitch
    }

    /**
     * 중앙 위치를 반환합니다.
     * 월드를 불러오지 않았을 경우 오류가 발생합니다.
     */
    fun getCenterLocation(): Location {
        Preconditions.checkNotNull(Bukkit.getWorld(centerWorldName), "아직 월드를 불러오지 않았습니다.")
        return Location(Bukkit.getWorld(centerWorldName), centerX, centerY, centerZ, centerYaw, centerPitch)
    }

    fun getCenterLocationString(): String? {
        return "$centerWorldName, $centerX, $centerY, $centerZ, $centerYaw, $centerPitch"
    }

    /**
     * 해당 위치가 맵 안인지 여부를 반환합니다.
     */
    fun isInRadius(location: Location): Boolean {
        val centerLocation = getCenterLocation()
        Preconditions.checkNotNull(centerLocation)
        Preconditions.checkNotNull(worldBorderSize)
        return (Math.abs(centerLocation.x - location.x) <= worldBorderSize!!
                && Math.abs(centerLocation.z - location.z) <= worldBorderSize)
    }

    /**
     * 맵 내의 랜덤 위치를 불러옵니다.
     */
    fun getRandomLocation(): Location? {
        val centerLocation = getCenterLocation()
        Preconditions.checkNotNull(centerLocation)
        Preconditions.checkNotNull(worldBorderSize)

        // 랜덤 x, z
        val x = (centerLocation.x + Math.random() * (worldBorderSize!! / 2)).toInt()
        val z = (centerLocation.z + Math.random() * (worldBorderSize / 2)).toInt()


        // 랜덤 위치에서 제일 높은 블럭 찾기
        val block = centerLocation.world.getHighestBlockAt(x, z) ?: return getRandomLocation()

        // 블럭이 없을 경우 다시 찾음
        when (block.type) {
            Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.BEDROCK, Material.BARRIER -> return getRandomLocation()
        }

        // 블럭보다 한 칸 높게 반환
        return block.location.add(0.0, 1.0, 0.0)
    }

    /**
     * 플레이어를 랜덤 위치로 텔레포트 시킵니다.
     */
    fun teleportToRandomLocation(player: Player?) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(GameCore.gamePlugin as JavaPlugin) { teleportToRandomLocation(player) }
            return
        }
        TeleportUtil.teleport(player, getRandomLocation())
    }

    /**
     * 경계선을 이 맵의 현재 경계선으로 설정합니다.
     */
    fun setWorldBorder() {
        val centerLocation = getCenterLocation()
        Preconditions.checkNotNull(centerLocation, "맵 위치가 설정되지 않았습니다.")
        Preconditions.checkNotNull(currentWorldBorderSize, "현재 맵 크기가 설정되지 않았습니다.")

        val worldBorder = centerLocation.world.worldBorder
        worldBorder.center = centerLocation
        worldBorder.size = currentWorldBorderSize!!.toDouble()
        worldBorder.damageBuffer = 0.0
    }

    /**
     * 경계선을 [newSize] 크기로 초당 [reduceSizePerSeconds] 칸만큼 변환시킵니다.
     */
    fun setWorldBorder(newSize: Int, reduceSizePerSeconds: Int): Int {
        val centerLocation = getCenterLocation()
        Preconditions.checkNotNull(centerLocation)
        Preconditions.checkNotNull(currentWorldBorderSize)
        Preconditions.checkArgument(newSize > 0, "새로운 경계선 크기는 0보다 커야합니다.")
        Preconditions.checkArgument(reduceSizePerSeconds > 0, "초당 줄어들 크기는 0보다 커야합니다.")

        val reduceDurationTime = Math.abs(newSize - worldBorderSize!!) / reduceSizePerSeconds // 줄어드는데 걸리는 시간
        currentWorldBorderSize = newSize

        val worldBorder = centerLocation.world.worldBorder
        worldBorder.center = centerLocation
        worldBorder.setSize(currentWorldBorderSize!!.toDouble(), reduceDurationTime.toLong())
        worldBorder.damageBuffer = 0.0

        return reduceDurationTime
    }

    /**
     * 맵을 Config에 저장합니다.
     */
    fun saveConfig() {
        GameCore.mapManager.saveConfig(this)
    }

    /**
     * 맵 Config를 다시 불러옵니다.
     */
    fun reloadConfig() {
        AnnoConfigModule.load(File(GameCore.mapManager.mapFolder, "$name.yml"), this)
    }

    /**
     * 맵 Config를 삭제합니다.
     */
    fun deleteConfig() {
        GameCore.mapManager.deleteConfig(this)
    }

    /**
     * 맵 월드가 로딩되어 있는지 확인합니다.
     */
    fun isLoaded(): Boolean {
        return Bukkit.getWorld(centerWorldName) != null
    }

    /**
     * SMW에서 맵 월드를 불러옵니다.
     */
    fun load() {
        if (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") == null) return

        // 기본 월드 제외
        if (centerWorldName == "world") return

        // 로딩 되어 있으면 안 함
        if (isLoaded()) return

        val plugin = Bukkit.getPluginManager().getPlugin("SlimeWorldManager") as SlimePlugin

        // 월드 불러오기
        val sqlLoader = plugin.getLoader("mysql")
        val properties = SlimePropertyMap()
        properties.setInt(SlimeProperties.SPAWN_X, centerX.toInt())
        properties.setInt(SlimeProperties.SPAWN_Y, centerY.toInt())
        properties.setInt(SlimeProperties.SPAWN_Z, centerZ.toInt())
        properties.setString(SlimeProperties.DIFFICULTY, "peaceful")
        properties.setBoolean(SlimeProperties.ALLOW_MONSTERS, true)
        properties.setBoolean(SlimeProperties.ALLOW_ANIMALS, true)
        properties.setBoolean(SlimeProperties.PVP, true)
        properties.setString(SlimeProperties.ENVIRONMENT, "NORMAL")
        properties.setString(SlimeProperties.WORLD_TYPE, "default")
        val slimeWorld = plugin.loadWorld(sqlLoader, centerWorldName, true, properties)

        Bukkit.getScheduler().runTask(GameCore.gamePlugin) {
            plugin.generateWorld(slimeWorld)
            initWorldBorderSettings()
            setWorldBorder()
        }
    }

    /**
     * SWM에서 맵 월드를 언로드합니다.
     */
    fun unload() {
        if (Bukkit.getPluginManager().getPlugin("SlimeWorldManager") == null) return

        // 기본 월드 제외
        if (centerWorldName == "world") return

        // 로딩 안되어 있으면 안 함
        if (!isLoaded()) return

        Bukkit.unloadWorld(centerWorldName, false)
    }

}