# ability

풀잎서버 **능력자(Ability) 게임** 플러그인 멀티 모듈 저장소입니다.
Kotlin 기반 Minecraft **Paper 1.8.8** 플러그인이며, Gradle(Kotlin DSL)로 빌드합니다.

능력 시스템의 핵심(추첨·할당·발동·쿨타임·지속·이벤트)을 코어가 제공하고, 개별 능력 구현·리플레이·보상은 코어에 의존하는 별도 모듈로 분리되어 있습니다.

---

## 모듈

| 모듈 | plugin.yml `name` | 역할 |
| --- | --- | --- |
| [`ability-core`](ability-core/README.md) | `Ability` | 능력 시스템 핵심 API·게임 로직 (추첨/할당/발동/쿨타임/지속/이벤트) |
| [`ability-pack-physicalfighters-reloaded`](ability-pack-physicalfighters-reloaded/README.md) | `PhysicalFightersReloaded` | 개별 능력 구현 모음(능력팩) |
| [`ability-replay`](ability-replay/README.md) | `AbilityReplay` | 능력 이벤트를 틱 단위로 녹화/재생 (GameReplay 위 어댑터) |
| [`ability-reward`](ability-reward/README.md) | `AbilityReward` | 킬/연속킬/어시스트/우승 게임머니 보상 |

> 새 능력은 거의 항상 능력팩(`ability-pack-*`)에서 추가합니다. `ability-core`는 베이스 클래스·타이머·추첨·발동 파이프라인 같은 인프라만 담습니다.

---

## 의존 관계

```
Core / GameCore / GameReplay / money      (사내 코어 플러그인, 서버에 함께 설치)
        ▲ compileOnly
   ability-core  (= Ability 플러그인, 능력 시스템 API)
        ▲ compileOnly project(":ability-core")
        ├── ability-pack-*   (능력 구현 = Ability 하위 클래스 모음)
        ├── ability-replay   (능력 이벤트 → 리플레이 데이터)
        └── ability-reward   (게임 이벤트 → 보상)
```

코어 플러그인(`Core`, `GameCore`, `GameReplay`, `net.pooleaf:*`, PhysicalFighters/BitAbility 등)은 모두 **`compileOnly`** 로만 의존하며 사내 Nexus(`repo.s8u.kr`)에서 받습니다. 즉 이 저장소의 산출물 JAR에는 코어가 포함되지 않으며, 서버에 코어 플러그인들이 함께 설치돼 있어야 동작합니다.

모듈 간 결합은 **능력 생명주기 이벤트**(`AbilityAssignEvent`, `AbilityCooldownStartEvent` 등)로만 이뤄집니다. `ability-replay`·`ability-reward`는 이 이벤트를 구독합니다.

---

## 빌드

```bash
./gradlew build                                          # 전체 모듈 빌드
./gradlew clean build                                    # 클린 후 전체 검증
./gradlew :ability-core:shadowJar                        # 특정 모듈만 shaded JAR (결과: <module>/build/libs)
./gradlew :ability-core:publish                          # Nexus 배포 (NEXUS_USERNAME / NEXUS_PASSWORD 환경변수 필요)
```

- JVM 타깃은 전 모듈 `1.8` 고정. Gradle 7.3.3, Kotlin 1.8.10.
- 테스트 프레임워크는 없습니다. 검증은 **컴파일 통과 + Paper 테스트 서버 수동 검증**으로 합니다.
- `processResources`가 `plugin.yml`의 `$pluginName / $version / $main`을 각 모듈 `gradle.properties` 값으로 치환합니다. 버전은 `gradle.properties`의 `version`에서만 바꿉니다.
- 로컬 서버 반영용 `copyToServerWindows` / `copyToServerMac` 태스크는 **경로가 개인 환경에 하드코딩**되어 있으니 실행 전 확인하세요.

---

## 시작하기

| 하려는 일 | 참고 |
| --- | --- |
| 새 능력 / 능력팩 만들기 | [`ability-core/docs/creating-ability-pack.md`](ability-core/docs/creating-ability-pack.md) |
| 능력 시스템 API 구조 파악 | [`ability-core/README.md`](ability-core/README.md) |
| 능력 리플레이 녹화/재생 동작 이해 | [`ability-replay/README.md`](ability-replay/README.md) |
| 보상 금액·우승 수식 설정 | [`ability-reward/README.md`](ability-reward/README.md) |
| 저장소 전체 규칙(에이전트/기여) | [`CLAUDE.md`](CLAUDE.md) |

---

## 컨벤션

- 패키지 루트는 `net.pooleaf.*`. 능력팩 능력 클래스는 모듈의 `abilities/`에 한 파일 = 한 능력으로 둡니다.
- 커밋 메시지·문서는 한국어. 커밋은 기존 히스토리 포맷(conventional commit, 한글 본문)을 따릅니다.
- 타이머 콜백은 비동기 스코프에서 동작하므로, Bukkit API 조작은 반드시 `BukkitSyncScope.launch { }`(메인 스레드)로 감쌉니다. 자세한 규칙은 능력팩 가이드를 참고하세요.
