# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

풀잎서버(Minecraft Paper 1.8.8)의 **능력자(Ability) 게임 플러그인** 멀티 모듈 저장소입니다. Kotlin / Gradle(Kotlin DSL) 기반.

## 빌드 / 자주 쓰는 명령

```bash
./gradlew build                                          # 전체 모듈 빌드
./gradlew clean build                                    # 클린 후 전체 검증
./gradlew :ability-core:shadowJar                        # 특정 모듈만 shaded JAR (결과: <module>/build/libs)
./gradlew :ability-pack-physicalfighters-reloaded:shadowJar
./gradlew :ability-core:publish                          # Nexus 배포 (NEXUS_USERNAME / NEXUS_PASSWORD 환경변수 필요)
```

- 테스트 프레임워크는 없음. 검증은 **컴파일 통과 + Paper 테스트 서버 수동 검증**으로 한다.
- JVM 타깃은 전 모듈 `1.8` 고정. Gradle 7.3.3, Kotlin 1.8.10.
- `processResources`가 `plugin.yml`의 `$pluginName / $version / $main`을 각 모듈 `gradle.properties` 값으로 치환한다. 버전은 `gradle.properties`의 `version`에서만 바꾼다.
- 로컬 서버 반영용 `copyToServerWindows` / `copyToServerMac` 태스크는 **경로가 개인 환경에 하드코딩**되어 있으니 실행 전 확인할 것.

## 모듈 구성

| 모듈 | plugin.yml `name` | 역할 | 의존(서버 설치) |
| --- | --- | --- | --- |
| `ability-core` | `Ability` | 능력 시스템 핵심 API·게임 로직 (추첨/할당/발동/쿨타임/지속/이벤트) | Core, GameCore, GameReplay |
| `ability-pack-physicalfighters-reloaded` | `PhysicalFightersReloaded` | 개별 능력 구현 모음(능력팩). 24종 | Core, **Ability**, GameCore |
| `ability-replay` | `AbilityReplay` | 능력 이벤트를 틱 단위 녹화/재생 (GameReplay 위 어댑터) | Core, GameReplay, (softdepend) Ability |
| `ability-reward` | `AbilityReward` | 킬/연속킬/어시스트/우승 게임머니 보상 | Core, **Ability**, GameCore, money |

의존 코어 플러그인(`Core`, `GameCore`, `GameReplay`, `net.pooleaf:*`, PhysicalFighters/BitAbility 등)은 모두 **`compileOnly`** 로만 잡혀 있고 사내 Nexus(`repo.s8u.kr`)에서 받는다. 즉 이 저장소의 산출물 JAR에는 코어가 포함되지 않으며, 서버에 코어 플러그인들이 함께 설치돼 있어야 동작한다.

## 아키텍처 — 의존 방향

```
Core / GameCore / GameReplay / money      (사내 코어 플러그인, 서버에 함께 설치)
        ▲ compileOnly
   ability-core  (= Ability 플러그인, 능력 시스템 API)
        ▲ compileOnly project(":ability-core")
        ├── ability-pack-*   (능력 구현 = Ability 하위 클래스 모음)
        ├── ability-replay   (능력 이벤트 → 리플레이 데이터)
        └── ability-reward   (게임 이벤트 → 보상)
```

**능력 추가는 거의 항상 `ability-pack-*` 모듈에서 한다.** `ability-core`는 베이스 클래스·타이머·추첨·발동 파이프라인 같은 인프라만 담는다.

### 핵심 흐름 (반드시 이해할 것)

- **진입점**은 각 모듈의 `*Plugin : BukkitCorePlugin`. `onStart()`에서 API `init()` → 능력/리스너/명령어 등록. 능력팩은 `onStart()`에서 `AbilityApi.unsafe.abilityManager.registerAbilities(this)` 한 줄로 **JAR 내 모든 `Ability` 하위 클래스를 리플렉션 자동 등록**한다. 따라서 능력 클래스의 `init {}`에서 `pluginName / name / rank / type / description`을 채워야만 등록된다(`isInitialized` 가드).
- **퍼사드는 `AbilityApi`**: `AbilityApi.game.{isGameStarted, isEnded, isGodMode}`, `AbilityApi.unsafe.abilityManager`(등록/조회), `AbilityApi.unsafe.playerManager`, 각종 config.
- **발동 → 쿨타임은 코어가 자동 처리한다.** 액티브(아이템) 능력은 `onCastByItem(...)`에서 `true`만 반환하면 코어(`AbilityCastListener`)가 쿨타임/지속타이머를 시작한다. 직접 `cooldownTimer.start()`를 호출하는 것은 **패시브(`Listener` 기반) 능력뿐**이다.
- `Ability`가 `Listener`를 구현하면 할당 시 자동 `registerEvents`, 해제 시 자동 해제된다. 수동 등록하지 말 것.
- 능력 생명주기는 `AbilityAssignEvent / AbilityResignEvent / AbilityCooldown(Start|End)Event / AbilityDuration(Start|End)Event / AbilityDrawCompleteEvent`로 발행되며, `ability-replay`·`ability-reward`가 이 이벤트들을 구독한다(모듈 간 결합은 이벤트로만).

### 코루틴/스레드 규칙 (어기면 런타임 깨짐)

- 타이머 콜백(`CoolDownTimer`/`DurationTimer`/`SimpleTimer`의 `onStart/onRun/onEnd`)은 **비동기 스코프**에서 돈다.
- 플레이어·월드 등 **Bukkit API 조작은 반드시 `BukkitSyncScope.launch { }`(메인 스레드)** 로 감싼다. 비동기는 `BukkitAsyncScope.launch { }`.
- 오프라인 가능성이 있는 플레이어에게는 `abilityPlayer?.playSoundSafely / sendMessageSafely / sendWarningSafely` 같은 `*Safely` 유틸을 쓴다.

## 능력 작성 패턴 (상세는 docs 참조)

발동 방식별 구현 인터페이스:

| 발동 방식 | 구현 | 쿨타임 시작 |
| --- | --- | --- |
| 아이템 클릭 (액티브) | `CastByItemHandler` (+ `Cooldownable`) | 코어 자동 (`onCastByItem` → `true`) |
| 지속형 (액티브) | `Durationable` | 지속 종료 후 코어 자동 |
| 이벤트 발동 (패시브) | `Listener` (+ `Cooldownable`) | 직접 `cooldownTimer.start()` |

등급(`AbilityRank`): `HIDDEN`(기본 추첨 제외), `SS / S / A / B / C`. 타입(`AbilityType`): `ACTIVE / PASSIVE`.

`ability-pack-physicalfighters-reloaded`는 등록 후 **같은 이름의 PhysicalFighters 원본 능력을 `ban = true`로 밴 처리**해 중복을 막는다(능력 추가/변경 시 이 동작 유의).

## 권장 참고 문서 (이 저장소가 직접 작성·유지)

- 능력팩/능력 작성 풀가이드(4패턴·타이머·체크리스트·템플릿): `ability-core/docs/creating-ability-pack.md`
- 모듈별 상세: 각 모듈의 `README.md` (`ability-core`, `ability-pack-physicalfighters-reloaded`, `ability-replay`, `ability-reward`)
- 능력 리플레이 데이터 추가법: `ability-replay/README.md` (저수준 녹화/재생 엔진 자체는 `game-replay` 저장소)
- `ability-idea.txt` / `memo.txt`: 능력 아이디어 모음과 게임 플로우·DB 스키마 작업 메모(비공식, 참고용)

## 컨벤션

- 응답·커밋 메시지·문서는 한국어. 커밋은 기존 히스토리 포맷(conventional commit, 한글 본문)을 따른다. `Co-Authored-By`는 넣지 않는다.
- 패키지 루트는 `net.pooleaf.*`. 능력팩 능력 클래스는 모듈의 `abilities/`에 한 파일=한 능력으로 둔다.
