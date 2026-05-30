# 능력팩 만들기 가이드

`ability-core`를 활용해 새로운 능력팩(Ability Pack) 플러그인을 만드는 방법을 설명합니다.

> 기준 모듈: `ability-core`
> 참고 예제: `ability-pack-physicalfighters-reloaded`

---

## 1. 개념 정리

### 능력팩이란?

능력팩은 `ability-core`에 의존하는 **독립된 Paper 플러그인**입니다.
하나의 능력팩 안에는 여러 개의 `Ability`(능력) 클래스가 들어가고, 플러그인이 켜질 때 이 능력들을 코어에 등록합니다.

```
ability-core (코어 / API)
   ▲
   │ compileOnly 의존
   │
ability-pack-xxx (능력팩 = 여러 Ability 모음)
```

### 핵심 구성 요소

| 요소 | 역할 |
| --- | --- |
| `Ability` | 모든 능력의 베이스 클래스. 이름·등급·타입·설명을 가진다. |
| `AbilityType` | `ACTIVE`(액티브) / `PASSIVE`(패시브) |
| `AbilityRank` | 능력 등급 `HIDDEN, SS, S, A, B, C` |
| `CastByItemHandler` | 아이템 우클릭/좌클릭으로 발동하는 액티브 능력용 인터페이스 |
| `Cooldownable` | 쿨타임을 가지는 능력용 인터페이스 |
| `Durationable` | 지속시간을 가지는 능력용 인터페이스 |
| `Listener` (Bukkit) | 이벤트로 발동하는 패시브 능력용 |
| `AbilityManager` | 능력 등록/조회를 담당. `AbilityApi.unsafe.abilityManager`로 접근 |

---

## 2. 능력팩 모듈 생성

### 2-1. 모듈 등록

루트 `settings.gradle.kts`에 새 모듈을 추가합니다.

```kotlin
rootProject.name = "ability"
include("ability-core")
include("ability-replay")
include("ability-reward")
include("ability-pack-physicalfighters-reloaded")
include("ability-pack-myserver")   // ← 추가
```

### 2-2. build.gradle.kts

`ability-pack-physicalfighters-reloaded/build.gradle.kts`를 그대로 따릅니다.
핵심은 **`ability-core`를 `compileOnly`로 의존**한다는 점입니다. (코어는 서버에 이미 올라가 있으므로 shade 하지 않음)

```kotlin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("maven-publish")
}

dependencies {
    compileOnly(project(":ability-core"))
    // 필요한 추가 의존성만 compileOnly 로 선언
    // compileOnly("net.pooleaf:money:1.0.0")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"   // JVM 타깃 1.8 고정
    }

    // plugin.yml 안의 $변수를 gradle 프로퍼티로 치환
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
    }

    withType<ShadowJar> {
        delete("build/resources")
        archiveClassifier.set("")
    }
}
```

### 2-3. plugin.yml

`src/main/resources/plugin.yml`. **반드시 `Ability`를 `depend`에 넣습니다.**

```yaml
name: $pluginName
version: $version
main: $main
depend:
  - Core
  - Ability       # ← ability-core 플러그인 (필수)
  - GameCore
```

> `$pluginName`, `$version`, `$main`은 빌드 시 gradle 프로퍼티(`gradle.properties` 또는 모듈 설정)로 치환됩니다. 기존 모듈의 값을 참고해 동일한 형식으로 채우세요.

### 2-4. 디렉토리 구조

```
ability-pack-myserver/
├── build.gradle.kts
└── src/main/
    ├── kotlin/net/pooleaf/ability/pack/myserver/
    │   ├── MyServerPackPlugin.kt        ← 플러그인 진입점
    │   └── abilities/                   ← 능력 클래스 모음
    │       ├── ThunderBolt.kt
    │       └── ...
    └── resources/plugin.yml
```

---

## 3. 플러그인 진입점 작성

`BukkitCorePlugin`을 상속하고, `onStart()`에서 **`registerAbilities(this)`** 를 호출하면
해당 플러그인의 모든 `Ability` 하위 클래스가 리플렉션으로 자동 등록됩니다.

```kotlin
package net.pooleaf.ability.pack.myserver

import net.pooleaf.ability.AbilityApi
import net.pooleaf.core.modules.support.common.CommonChatColor
import net.pooleaf.core.plugin.BukkitCorePlugin

class MyServerPackPlugin : BukkitCorePlugin() {

    companion object {
        lateinit var instance: MyServerPackPlugin
    }

    override fun onStart() {
        instance = this

        prefix = "§a[ MyServerPack ]"
        color = CommonChatColor.GREEN
        registerLoggerPrefix()

        // 이 플러그인의 모든 Ability 자동 등록
        AbilityApi.unsafe.abilityManager.registerAbilities(this)
    }
}
```

> `registerAbilities(plugin)`은 플러그인 JAR 안의 클래스를 스캔해 `Ability`를 상속하고 **초기화가 완료된**(`isInitialized`) 능력만 등록합니다. 따라서 능력 클래스의 `init {}` 블록에서 `name`, `rank`, `type`, `description`을 반드시 채워야 합니다. (`pluginName`도 함께)

---

## 4. 능력(Ability) 만들기

### 4-1. 공통 기본형

모든 능력은 `Ability`를 상속하고 `init` 블록에서 메타데이터를 채웁니다.

```kotlin
class MyAbility : Ability() {
    init {
        pluginName = MyServerPackPlugin.instance.name  // 소속 플러그인 이름
        name = "내 능력"                                 // 능력 이름 (고유)
        rank = AbilityRank.A                            // 등급
        type = AbilityType.ACTIVE                       // ACTIVE / PASSIVE
        description = listOf(                            // 능력 설명 (여러 줄)
            "이 능력은 ...",
            "두 번째 설명 줄",
        )
        ban = false                                     // true면 추첨/사용에서 제외
    }
}
```

| 필드 | 설명 |
| --- | --- |
| `pluginName` | 소속 플러그인 이름. `fullName`이 `pluginName:name`으로 만들어진다. |
| `name` | 능력 이름. 명령어 검색/표시에 사용. |
| `rank` | `AbilityRank` (HIDDEN/SS/S/A/B/C). `HIDDEN`은 기본 추첨에서 제외. |
| `type` | `AbilityType.ACTIVE` 또는 `PASSIVE`. (표시용 분류) |
| `description` | `/능력` 명령 등에서 보여줄 설명 줄들. |
| `ban` | `true`면 등록은 되지만 추첨/사용 후보에서 빠진다. |

생명주기 훅:

- `onAssign()` : 능력이 플레이어에게 할당될 때 (초기 셋업, 코루틴 시작 등)
- `onResign()` : 능력이 해제될 때 (정리, 코루틴 cancel 등)
- `canUse()` : 현재 게임 상태에서 능력 사용이 가능한지(protected). 패시브에서 가드로 사용.

> `Ability`가 `Listener`를 구현하면, 할당 시 자동으로 이벤트 리스너가 등록되고 해제 시 자동으로 해제됩니다. 별도로 `registerEvents`를 호출할 필요가 없습니다.

---

### 4-2. 액티브 능력 — 아이템으로 발동 (`CastByItemHandler`)

가장 일반적인 액티브 능력입니다. **철괴 우클릭** 같은 식으로 발동합니다.
`CastByItemHandler`는 `Cooldownable`을 포함하므로 `cooldownTimer`를 반드시 구현해야 합니다.

```kotlin
class ThunderBolt : Ability(), Listener, CastByItemHandler, Cooldownable {

    init {
        pluginName = MyServerPackPlugin.instance.name
        name = "썬더볼트"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 5칸 내의 적에게 6 데미지를 줍니다.",
        )
        ban = false
    }

    // 발동에 쓰이는 아이템 (손에 든 아이템과 isSimilar 비교)
    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    // 쿨타임 5초
    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 5_000L)

    // castItem으로 클릭했을 때 호출됨. true 반환 시 발동 성공 → 쿨타임 시작
    override fun onCastByItem(
        event: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        // 우클릭만 허용하고 싶으면:
        // if (clickType != CastByItemHandler.ClickType.RIGHT) return false

        val location = event.player.location
        event.player.getNearbyEntities(5.0, 5.0, 5.0).forEach { entity ->
            if (entity !is LivingEntity) return@forEach
            location.world.strikeLightningEffect(entity.location)
            entity.damageBypassAntiCheat(6.0, event.player)
        }

        return true  // 발동 성공
    }
}
```

**발동 흐름** (`AbilityCastListener`가 처리):

1. 게임이 진행 중이고, 종료/무적모드가 아닐 때만 동작
2. 손에 든 아이템이 `castItem`과 일치하는지(`isCastItem`) 확인
3. 쿨타임/지속시간이 돌고 있으면 "아직 능력을 사용할 수 없습니다" 경고 후 중단
4. `onCastByItem(...)` 호출
   - `true` 반환 시: 능력 발동으로 간주
     - `Durationable`이면 → `durationTimer.start()` (지속 종료 시 쿨타임 시작)
     - 아니면 → `cooldownTimer.start()`
   - `false` 반환 시: 아무 일도 없음 (쿨타임도 안 돔)

> 따라서 `onCastByItem` 안에서는 쿨타임을 직접 시작할 필요가 없습니다. `true`만 반환하면 코어가 알아서 처리합니다.

---

### 4-3. 액티브 능력 — 지속시간 추가 (`Durationable`)

발동 후 일정 시간 동안 효과가 유지되는 능력입니다.
`DurationTimer`를 `object`로 오버라이드해 `onStart()/onEnd()`에 효과를 넣습니다.

```kotlin
class Fly : Ability(), Listener, CastByItemHandler, Cooldownable, Durationable {

    init {
        pluginName = MyServerPackPlugin.instance.name
        name = "플라이"
        rank = AbilityRank.S
        type = AbilityType.ACTIVE
        description = listOf(
            "철괴 클릭 시 능력을 사용합니다.",
            "능력 사용 시 10초간 하늘을 날 수 있습니다.",
            "낙하 데미지를 받지 않습니다.",
        )
        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 80_000L)

    // 지속시간 10초
    override val durationTimer: DurationTimer = object : DurationTimer(this, 10_000L) {
        override fun onStart() {
            super.onStart()
            // 메인 스레드에서 플레이어 상태 변경
            BukkitSyncScope.launch {
                abilityPlayer?.player?.let { p ->
                    p.allowFlight = true
                    p.isFlying = true
                }
            }
        }

        override fun onEnd() {
            super.onEnd()
            BukkitSyncScope.launch {
                abilityPlayer?.player?.let { p ->
                    p.allowFlight = false
                    p.isFlying = false
                }
            }
        }
    }

    override fun onCastByItem(
        event: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean = true   // 발동만 알리면 됨. 효과는 durationTimer가 처리

    // 지속 중 낙하 데미지 무시
    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (!AbilityApi.game.isGameStarted || AbilityApi.game.isGodMode) return
        if (abilityPlayer?.player != event.entity) return
        if (event.cause != EntityDamageEvent.DamageCause.FALL) return
        event.isCancelled = true
    }
}
```

**중요한 자동 동작:**

- `Durationable` + `Cooldownable`을 함께 구현하면, **지속시간이 끝나는 순간(`DurationTimer.onEnd`) 자동으로 쿨타임이 시작**됩니다.
- 즉 "발동 → 지속 → (끝) → 쿨타임" 흐름이 코어에서 자동으로 이어집니다.
- 플레이어 객체 조작(`allowFlight`, `isFlying`, 텔레포트 등)은 반드시 `BukkitSyncScope.launch { }`(메인 스레드)에서 하세요. 타이머는 비동기 스코프에서 돕니다.

---

### 4-4. 패시브 능력 — 이벤트로 발동 (`Listener`)

플레이어가 따로 발동하지 않고, 특정 상황에서 자동으로 작동하는 능력입니다.
`Listener`를 구현하고 `@EventHandler`로 처리합니다. 보통 `Cooldownable`을 함께 써서 남발을 막습니다.

```kotlin
class TestPassiveAbility : Ability(), Cooldownable, Listener {

    init {
        pluginName = MyServerPackPlugin.instance.name
        name = "반사 번개"
        rank = AbilityRank.C
        type = AbilityType.PASSIVE
        description = listOf("공격을 받으면 상대에게 번개가 칩니다.")
        ban = false
    }

    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 3_000L)

    @EventHandler
    fun onPlayerDamageByEntity(event: PlayerDamageByEntityEvent) {
        if (!canUse()) return                          // 게임 상태 가드

        abilityPlayer?.player?.let { player ->
            if (!event.player.equals(player)) return   // 내 능력 소유자인지 확인
            if (remainingCooldownMillis > 0) return    // 쿨타임 체크

            cooldownTimer.start()                      // 패시브는 쿨타임 직접 시작
            event.damager.world.strikeLightning(event.damager.location)
        }
    }
}
```

**패시브 작성 시 주의:**

- 이벤트 핸들러 안에서 **반드시 능력 소유자 본인인지 확인**하세요 (`event.player == abilityPlayer?.player`). 리스너는 모든 플레이어의 이벤트를 받습니다.
- 패시브는 코어가 쿨타임을 자동으로 시작해주지 않으므로 `cooldownTimer.start()`를 직접 호출합니다.
- `canUse()` 또는 `AbilityApi.game.isGameStarted` 등으로 게임 상태를 가드하세요.

---

## 5. 타이머 (Cooldown / Duration)

### CoolDownTimer

```kotlin
override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 5_000L)  // 5초
```

- 시작/진행/종료 시 액션바로 남은 시간과 효과음을 자동 표시합니다.
- `AbilityCooldownStartEvent`, `AbilityCooldownEndEvent`를 자동 발행합니다.
- 조회: `cooldownMillis`(전체), `remainingCooldownMillis`(남은 시간), `cooldownTimer.isRunning`.

### DurationTimer

```kotlin
override val durationTimer: DurationTimer = object : DurationTimer(this, 10_000L) {
    override fun onStart() { super.onStart(); /* 효과 시작 */ }
    override fun onRun()   { super.onRun();   /* intervalMillis마다 반복 (기본 100ms) */ }
    override fun onEnd()   { super.onEnd();   /* 효과 종료 */ }
}
```

- 액션바로 남은 지속시간을 자동 표시합니다.
- `onEnd()`에서 능력이 `Cooldownable`이면 자동으로 쿨타임을 시작합니다.
- `AbilityDurationStartEvent`, `AbilityDurationEndEvent`를 자동 발행합니다.

### 직접 커스텀 타이머가 필요할 때

`SimpleTimer(timeMillis, intervalMillis)`를 상속하면 `onStart / onRun / onEnd / onCancel` 훅으로 임의의 반복 로직을 만들 수 있습니다. 단, 타이머 콜백은 **비동기 스코프**에서 실행되므로 Bukkit API 호출은 `BukkitSyncScope.launch`로 감싸세요.

---

## 6. 능력 안에서 자주 쓰는 객체

| 접근 | 용도 |
| --- | --- |
| `abilityPlayer` | 능력을 가진 플레이어(`AbilityPlayer?`). `abilityPlayer?.player`로 Bukkit `Player`. |
| `abilityPlayer?.isOnline` | 온라인 여부 |
| `abilityPlayer?.playSoundSafely(...)` | 안전한 효과음 재생 |
| `abilityPlayer?.sendMessageSafely(...)` / `sendWarningSafely(...)` | 안전한 메시지 전송 |
| `AbilityApi.game.isGameStarted / isEnded / isGodMode` | 게임 상태 확인 |
| `AbilityApi.unsafe.abilityManager` | 능력 등록/조회 |

코루틴 스코프:

- `BukkitSyncScope.launch { }` : **메인 스레드**. 플레이어/월드 등 Bukkit API 조작.
- `BukkitAsyncScope.launch { }` : 비동기. (타이머 내부가 사용)

---

## 7. 팀 대응 (중요)

`ability-core`는 `GameCore`의 **팀(Team)** 위에서 동작합니다. 능력 게임은 팀전으로 진행될 수 있으므로,
**다른 플레이어에게 영향을 주는 능력은 반드시 아군(같은 팀)을 대상에서 제외**해야 합니다.

> 직접적인 손 PVP는 `GameCore`의 `TeamPlayerPvpListener`가 자동으로 막아줍니다.
> 하지만 **능력 효과(광역 데미지·끌어당김·디버프·순간이동·넉백 등)는 코어가 걸러주지 않습니다.**
> 능력 코드 안에서 직접 팀을 확인해 아군을 제외하지 않으면 **아군을 죽이거나 방해**하게 됩니다.

### 팀 접근

`AbilityPlayer`는 `GamePlayer`를 상속하므로 `abilityPlayer.team`으로 바로 소속 팀에 접근할 수 있습니다.

| 접근 | 용도 |
| --- | --- |
| `abilityPlayer.team` | 능력 시전자의 소속 `Team?`. **`null`이면 무소속(단독)** |
| `gamePlayer.team` | 임의의 `GamePlayer`의 소속 팀 |
| `player.toGamePlayer()` | Bukkit `Player` → `GamePlayer?` (`net.pooleaf.gamecore.utils.toGamePlayer`) |
| `GameCore.unsafe.teamManager.getHasGamePlayer(gamePlayer)` | 플레이어가 속한 `Team?` 조회 |

### 같은 팀 판별

두 플레이어가 같은 팀인지는 `team`을 비교합니다.

```kotlin
// 같은 팀이면 true (둘 다 무소속이면 team == team == null 이라 true가 되니 주의)
abilityPlayer.team != null && abilityPlayer.team == target.team
```

> ⚠️ `team`이 `null`(무소속)인 플레이어끼리는 `team == team`이 `null == null`로 **true**가 됩니다.
> 무소속끼리는 서로 적이어야 한다면 `abilityPlayer.team != null && abilityPlayer.team == it.team` 처럼 `null` 가드를 먼저 두세요.

### 패턴 1 — `playerManager`로 대상 목록을 만들 때 (권장)

광역 능력은 `getOnlinePlayingPlayers()`로 대상 후보를 모은 뒤 **팀과 거리로 필터링**합니다.
(실제 능력 `Pagi`가 쓰는 방식)

```kotlin
private fun damageNearPlayers() {
    val abilityPlayer = abilityPlayer ?: return

    GameCore.unsafe.playerManager.getOnlinePlayingPlayers()
        .filter { abilityPlayer.team != it.team }                       // ← 아군 제외
        .filter { abilityPlayer.player.location.distance(it.player.location) <= 10 }
        .forEach {
            it.player.damageBypassAntiCheat(5.0, abilityPlayer.player)
            it.player.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 30, 0), true)
        }
}
```

### 패턴 2 — `getNearbyEntities` 결과를 처리할 때

Bukkit 엔티티 목록을 직접 다룰 때는 `toGamePlayer()`로 변환한 뒤 팀을 비교합니다.

```kotlin
val me = abilityPlayer ?: return
event.player.getNearbyEntities(5.0, 5.0, 5.0).forEach { entity ->
    if (entity !is Player) return@forEach
    val target = entity.toGamePlayer() ?: return@forEach
    if (me.team != null && me.team == target.team) return@forEach        // ← 아군 제외
    // ... 효과 적용
}
```

> 시전자 자신(`entity == me.player`)도 대상에서 빼야 한다면 팀 체크와 함께 본인 여부도 확인하세요.

---

## 9. 빌드 & 배포

```bash
# 전체 빌드
./gradlew build

# 능력팩만 shaded JAR 생성 (결과물: build/libs)
./gradlew :ability-pack-myserver:shadowJar

# 클린 후 전체 검증
./gradlew clean build
```

- 능력팩 JAR과 함께 **`ability-core`(Ability), Core, GameCore 플러그인이 서버에 함께 설치**되어 있어야 합니다.
- 능력팩은 `ability-core`를 `compileOnly`로 쓰므로 자체 JAR에 코어가 포함되지 않습니다.
- 로컬 서버 반영용 `copyToServerWindows`/`copyToServerMac` 태스크 경로는 개인 환경에 고정되어 있으므로 사용 전 확인하세요.

---

## 10. 체크리스트

새 능력을 추가할 때:

- [ ] `Ability` 상속, `init`에서 `pluginName / name / rank / type / description` 채움
- [ ] 발동 방식에 맞는 인터페이스 구현
  - 아이템 발동 액티브 → `CastByItemHandler` (+ `Cooldownable`)
  - 지속형 → `Durationable`
  - 이벤트 발동 패시브 → `Listener`
- [ ] 액티브: `onCastByItem`에서 성공 시 `true` 반환 (쿨타임은 코어가 시작)
- [ ] 패시브: 소유자 본인 확인 + `cooldownTimer.start()` 직접 호출
- [ ] **다른 플레이어에게 영향을 주는 효과(광역 데미지·끌어당김·디버프·순간이동 등)는 아군(같은 팀)을 대상에서 제외** (`abilityPlayer.team` 비교, → [7장](#7-팀-대응-중요))
- [ ] Bukkit API 조작은 `BukkitSyncScope.launch`로 감쌈
- [ ] `onResign()`에서 코루틴/상태 정리 (`onAssign`에서 시작한 것)
- [ ] `./gradlew :ability-pack-xxx:build`로 컴파일 확인
- [ ] Paper 테스트 서버에서 발동/쿨타임/지속 수동 검증 (**팀전에서 아군에게 효과가 안 가는지 포함**)

---

## 11. 빠른 시작 템플릿

```kotlin
package net.pooleaf.ability.pack.myserver.abilities

import net.pooleaf.ability.ability.*
import net.pooleaf.ability.ability.cast.CastByItemHandler
import net.pooleaf.ability.ability.timer.CoolDownTimer
import net.pooleaf.ability.pack.myserver.MyServerPackPlugin
import org.bukkit.Material
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class TemplateAbility : Ability(), Listener, CastByItemHandler, Cooldownable {

    init {
        pluginName = MyServerPackPlugin.instance.name
        name = "템플릿"
        rank = AbilityRank.B
        type = AbilityType.ACTIVE
        description = listOf("철괴 클릭 시 능력을 사용합니다.")
        ban = false
    }

    override val castItem: List<ItemStack> = listOf(ItemStack(Material.IRON_INGOT))
    override val cooldownTimer: CoolDownTimer = CoolDownTimer(this, 5_000L)

    override fun onCastByItem(
        event: PlayerInteractEvent,
        item: ItemStack,
        clickType: CastByItemHandler.ClickType
    ): Boolean {
        // TODO: 능력 효과 구현
        return true
    }
}
```
