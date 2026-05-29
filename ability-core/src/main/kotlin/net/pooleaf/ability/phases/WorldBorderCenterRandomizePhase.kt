package net.pooleaf.ability.phases

import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.phase.Phase
import org.bukkit.Location
import kotlin.random.Random

/**
 * 게임 시작 전 자기장 중심을 맵 가운데 1/3 영역 안에서 랜덤으로 결정합니다.
 * ability-game-config.yml의 "경계선 랜덤" 설정이 꺼져 있으면 기존 맵 중심을 그대로 사용합니다.
 *
 * [StartCountPhase] 바로 앞에 실행되므로 텔레포트 전에 중심이 확정됩니다.
 * 이를 통해 이벤트 리스너와 텔레포트 사이의 실행 순서 경합을 제거합니다.
 */
class WorldBorderCenterRandomizePhase : Phase() {

    override suspend fun onStart() {
        if (!AbilityApi.abilityGameConfig.useRandomWorldBorderCenter) return

        val map = GameCore.currentMap ?: return
        val center = map.centerLocation ?: return

        // 맵의 가운데 1/3 영역에서 균등 분포로 샘플링한다.
        // offset_max = worldBorderSize / 6 이면 중심에서 맵 가장자리까지 항상 최소 worldBorderSize / 3 의 여유가 보장된다.
        val maxOffset = map.worldBorderSize / 6
        val offsetX = if (maxOffset > 0) Random.nextInt(-maxOffset, maxOffset + 1) else 0
        val offsetZ = if (maxOffset > 0) Random.nextInt(-maxOffset, maxOffset + 1) else 0

        // x, z만 랜덤화하고 y / yaw / pitch는 맵 원본 중심 값을 그대로 사용한다.
        val newCenter = Location(
            center.world,
            center.x + offsetX,
            center.y,
            center.z + offsetZ,
            center.yaw,
            center.pitch,
        )

        map.currentWorldBorderCenterLocation = newCenter

        // worldBorder 조작은 메인 스레드에서 해야 하므로 BukkitSyncScope로 감싸고 join()으로 완료를 보장한다.
        BukkitSyncScope.launch {
            map.updateWorldBorder(map.currentWorldBorderSize)
        }.join()
    }

}
