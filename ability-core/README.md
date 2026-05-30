# ability-core

풀잎서버 **능력 시스템의 핵심 API·게임 로직 모듈**입니다.
Kotlin 기반 Minecraft Paper 1.8.8 플러그인으로, 능력(Ability)의 추첨·할당·발동·쿨타임·지속시간·이벤트를 모두 담당합니다.

개별 능력 구현은 이 모듈에 의존하는 **능력팩**(예: `ability-pack-physicalfighters-reloaded`)에서 작성합니다.

> 새 능력팩을 만들려면 → [docs/creating-ability-pack.md](docs/creating-ability-pack.md)

---

## 역할

- 능력 베이스 클래스(`Ability`)와 발동/타이머 인터페이스 제공
- 능력 등록·조회 관리 (`AbilityManager`)
- 아이템 클릭 기반 액티브 능력 발동 처리 (`AbilityCastListener`)
- 쿨타임/지속시간 타이머 및 액션바 표시 (`CoolDownTimer`, `DurationTimer`)
- 능력 추첨·재추첨, 사이드바, 명령어, 블랙리스트 설정
- 능력 생명주기 이벤트 발행
- 외부 능력 플러그인 호환 레이어(`compat`: PhysicalFighters, AbilityWar, BitAbility)

---

## 의존 관계

```
Core, GameCore, GameReplay   ← ability-core 가 의존하는 코어 플러그인
        ▲
        │ (서버에 함께 설치)
   ability-core (Ability)
        ▲
        │ compileOnly
        │
   ability-pack-xxx (능력 구현)
```

`plugin.yml`:

```yaml
name: $pluginName     # Ability
depend:
  - Core
  - GameCore
  - GameReplay
```

서버에는 `Core`, `GameCore`, `GameReplay`, `Ability`(이 모듈), 그리고 능력팩 JAR이 함께 설치되어야 합니다.

---

## 핵심 구조

| 패키지 | 내용 |
| --- | --- |
| `ability` | `Ability` 베이스, `AbilityType`, `AbilityRank`, `Cooldownable`, `Durationable`, `AbilityManager`, `AbilityService` |
| `ability.cast` | `CastByItemHandler` — 아이템 클릭 발동 인터페이스 |
| `ability.timer` | `SimpleTimer`, `CoolDownTimer`, `DurationTimer` |
| `player` | `AbilityPlayer`, `AbilityPlayerManager`, `AbilityPlayerFactory` |
| `game` | `AbilityGame`, `AbilityPhasePipeline` — 게임 흐름/단계 |
| `phases` | `AbilityDrawPhase`(능력 추첨), `WorldBorderCenterRandomizePhase` |
| `listeners` | 능력 발동/캐스트/패배/재추첨 등 이벤트 리스너 |
| `commands` | `AbilityCommand`, `AdminAbilityCommand`, `AdminTestCommand` |
| `event` | 능력/게임 생명주기 이벤트 |
| `compat` | 외부 능력 플러그인 호환 어댑터 |
| `sidebar` | 능력 정보 사이드바 |
| `configs` | 게임 설정, 능력 블랙리스트 설정 |

### 주요 진입점

- **`AbilityApi`** — 능력 시스템의 퍼사드.
  - `AbilityApi.game` : 현재 게임 상태 (`isGameStarted`, `isEnded`, `isGodMode`)
  - `AbilityApi.unsafe.abilityManager` : 능력 등록/조회
  - `AbilityApi.unsafe.playerManager` : 능력 플레이어 조회
  - `AbilityApi.abilityBlacklistConfig`, `abilityGameConfig` : 설정
- **`AbilityPlugin`** — `BukkitCorePlugin` 진입점. `onStart()`에서 `GameCore.init` → `AbilityApi.init` → 능력/호환 플러그인/리스너/명령어 등록.

---

## 능력(Ability) 한눈에 보기

모든 능력은 `Ability`를 상속하고 `init`에서 메타데이터를 채웁니다.

```kotlin
class ThunderBolt : Ability(), Listener, CastByItemHandler, Cooldownable {
    init {
        pluginName = MyPackPlugin.instance.name
        name = "썬더볼트"
        rank = AbilityRank.A          // HIDDEN, SS, S, A, B, C
        type = AbilityType.ACTIVE     // ACTIVE / PASSIVE
        description = listOf("철괴 클릭 시 5칸 내 적에게 6 데미지")
        ban = false                   // true면 추첨/사용에서 제외
    }

    override val castItem = listOf(ItemStack(Material.IRON_INGOT))
    override val cooldownTimer = CoolDownTimer(this, 5_000L)

    override fun onCastByItem(event: PlayerInteractEvent, item: ItemStack,
                              clickType: CastByItemHandler.ClickType): Boolean {
        // ... 효과 구현
        return true   // true 반환 시 코어가 쿨타임 자동 시작
    }
}
```

발동 방식별 구현 인터페이스:

| 발동 방식 | 구현 인터페이스 | 쿨타임 시작 |
| --- | --- | --- |
| 아이템 클릭 (액티브) | `CastByItemHandler` (+`Cooldownable`) | 코어가 자동 |
| 지속형 (액티브) | `Durationable` | 지속 종료 후 코어가 자동 |
| 이벤트 발동 (패시브) | `Listener` (+`Cooldownable`) | 직접 `cooldownTimer.start()` |

자세한 작성법과 4가지 패턴(아이템/지속/패시브/커스텀 타이머)은
**[docs/creating-ability-pack.md](docs/creating-ability-pack.md)** 를 참고하세요.

---

## 등급 (AbilityRank)

| 등급 | 비고 |
| --- | --- |
| `HIDDEN` | 기본 추첨 후보에서 제외 |
| `SS` / `S` / `A` / `B` / `C` | 일반 등급 |

---

## 이벤트

`org.bukkit.event.Listener`로 구독할 수 있는 능력 생명주기 이벤트:

| 이벤트 | 발행 시점 |
| --- | --- |
| `AbilityAssignEvent` | 능력이 플레이어에게 할당될 때 |
| `AbilityResignEvent` | 능력이 해제될 때 |
| `AbilityCooldownStartEvent` / `AbilityCooldownEndEvent` | 쿨타임 시작/종료 |
| `AbilityDurationStartEvent` / `AbilityDurationEndEvent` | 지속시간 시작/종료 |
| `AbilityDrawCompleteEvent` | 능력 추첨 완료 |

---

## 명령어 / 권한

| 명령어 | 별칭 | 설명 |
| --- | --- | --- |
| `/능력자` | `ability`, `va`, `ha`, `ua`, `a` | 능력 관련 메인 명령 |
| `/능력` | `help` 등 | 능력 도움말/매뉴얼 |

관리자 명령은 권한 `ability.admin`(`AbilityPermission.ADMIN`)이 필요합니다.

---

## 빌드

```bash
# 전체 모듈 빌드
./gradlew build

# 이 모듈만 shaded JAR (결과물: build/libs)
./gradlew :ability-core:shadowJar

# 클린 후 전체 검증
./gradlew clean build
```

- JVM 타깃은 `1.8`.
- `processResources`가 `plugin.yml`의 `$pluginName / $version / $main`을 gradle 프로퍼티로 치환합니다.
- Nexus 배포: `NEXUS_USERNAME`, `NEXUS_PASSWORD` 환경 변수로만 자격 증명을 주입합니다. (`./gradlew :ability-core:publish`)
- 로컬 서버 반영용 `copyToServerWindows` / `copyToServerMac` 태스크는 경로가 개인 환경에 고정되어 있으니 사용 전 확인하세요.

---

## 참고 문서

- [능력팩 만들기 가이드](docs/creating-ability-pack.md)
- 저장소 전체 규칙: 루트 `CLAUDE.md`
