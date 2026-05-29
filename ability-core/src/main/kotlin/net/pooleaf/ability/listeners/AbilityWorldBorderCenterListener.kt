package net.pooleaf.ability.listeners

import kotlinx.coroutines.launch
import net.pooleaf.ability.AbilityApi
import net.pooleaf.core.modules.coroutine.bukkit.BukkitSyncScope
import net.pooleaf.gamecore.GameCore
import net.pooleaf.gamecore.events.game.GameStartedEvent
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.random.Random

/**
 * 게임 시작 시 자기장 중심을 맵 가운데 1/3 영역 안에서 랜덤으로 결정합니다.
 * ability-game-config.yml의 "경계선 랜덤" 설정이 꺼져 있으면 기존 맵 중심을 그대로 사용합니다.
 *
 * 중심은 게임 시작 시 단 한 번 결정되며, 결정된 이후 모든 자기장 단계(초기, 1차, 2차, 3차)가
 * 동일한 중심으로 줄어듭니다. (월드보더 정사각형 제약상 중심은 게임 도중 변경하지 않습니다.)
 *
 * [GameStartedEvent]는 플레이어가 맵에 텔레포트 되기 전, 그리고 1차 WorldBorderUpdatePhase가
 * 시작되기 한참 전에 발행됩니다. initWorldBorder()는 이미 기존 중심으로 초기 자기장을 그려두므로
 * 랜덤 중심을 세팅한 뒤 현재 크기로 월드보더를 한 번 다시 적용합니다.
 */
class AbilityWorldBorderCenterListener : Listener {

    @EventHandler
    fun onGameStarted(event: GameStartedEvent) {
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

        // initWorldBorder()는 GameStartedEvent보다 먼저 실행되어 초기 자기장을 맵 원본 중심으로 그려둔 상태다.
        // 새 중심을 세팅한 직후 현재 크기(초기 크기) 그대로 자기장을 다시 그려, 1차 단계 전부터
        // 초기 자기장이 새 중심을 따르도록 한다. (1차 단계 진입 시 발생하던 중심 순간이동 제거)
        //
        // GameStartedEvent는 async 스레드에서 발행되는데, worldBorder 조작은 메인 스레드에서 해야 하므로
        // BukkitSyncScope로 감싼다. (StartCountPhase의 teleportToMap 처리와 동일한 패턴)
        BukkitSyncScope.launch {
            map.updateWorldBorder(map.currentWorldBorderSize)
        }
    }
}
