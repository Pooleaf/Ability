# ability-replay

풀잎서버 **능력 리플레이 모듈**입니다.
게임 중 일어난 **능력 관련 사건**(능력 할당/해제, 쿨타임 시작/종료, 지속시간 시작/종료, 능력 추첨 완료)을 틱 단위로 **녹화**하고, 리플레이 재생 시 관전자에게 **재생**합니다.

저수준 녹화/재생 엔진은 `game-replay`(GameReplay) 코어가 담당하고, 이 모듈은 그 위에 **능력 도메인 데이터**를 얹는 어댑터 레이어입니다.

> 새 데이터 추가는 아래 [§새 데이터 추가하기](#새-데이터-추가하기) 참고.
> 녹화/재생 엔진 자체(새 `RecordData` 종류 등)는 `game-replay` 저장소에서 다룹니다.

---

## 역할

- `ability-core`가 발행하는 능력 이벤트를 듣고 리플레이 데이터로 **녹화**
- 녹화된 능력 데이터를 리플레이 재생 시 관전자에게 **표현**
- 능력 데이터 ↔ 재생 핸들러 **등록**

`ability-core`(능력 시스템) 자체에는 의존하지만(`softdepend`), 리플레이 기능은 GameReplay 엔진 위에서 동작합니다.

---

## 의존 관계

```
Core, GameReplay     ← 필수 (depend)
Ability              ← 선택 (softdepend, 능력 이벤트 소스)
        ▲
        │
   ability-replay
```

`plugin.yml`:

```yaml
name: $pluginName     # AbilityReplay
depend:
  - Core
  - GameReplay
softdepend:
  - Ability
```

`build.gradle.kts`는 `ability-core`를 `compileOnly`로 의존합니다 (능력 이벤트 타입 참조용).

---

## 구조

이 모듈은 데이터 한 종류마다 **3종 세트**로 구성됩니다.

```
data/
├── datas/          [1] Data         녹화할 값 정의 (RecordData 구현)
│   ├── ability/    능력 할당/해제, 쿨타임/지속시간 시작·종료
│   └── game/       능력 추첨 완료
├── records/        [2] RecordListener   이벤트 → Data 기록
│   ├── ability/    *DataRecordListener
│   └── game/
└── replays/        [3] ReplayHandler    Data → 관전자에게 재생
    ├── ability/    *DataReplayHandler
    └── game/

replay/
└── AbilityReplayHandlerRegistry   재생 핸들러 등록
```

| 단계 | 클래스 | 책임 |
| --- | --- | --- |
| 1 | `*Data` (`data class`, `RecordData`) | 저장할 값. 원시값(UUID/String/숫자)만 담는다. |
| 2 | `*DataRecordListener` (`Listener`) | 능력 이벤트를 듣고 Data를 만들어 현재 틱에 기록 |
| 3 | `*DataReplayHandler` (`RecordDataReplayHandler<T>`) | 재생 시 관전자에게 표현 |

### 진입점

- **`AbilityReplayPlugin`** — `BukkitCorePlugin`. `onStart()`에서 `AbilityReplayApi.init()` 후, GameCore가 있으면 `registerEventListeners()`로 기록 리스너 등록.
- **`AbilityReplayApi`** — `init()` 시 `AbilityReplayHandlerRegistry.registerHandlers()`로 재생 핸들러를 GameReplay에 등록.
- **`AbilityReplayHandlerRegistry`** — `GameReplayApi.unsafe.recordDataManager.registerRecordData(...)` 호출 모음.

---

## 현재 지원하는 데이터

| 데이터 | type | 녹화 트리거(능력 이벤트) |
| --- | --- | --- |
| `AbilityAssignData` | `abilityAssign` | `AbilityAssignEvent` |
| `AbilityResignData` | (해제) | `AbilityResignEvent` |
| `AbilityCooldownStartData` | (쿨타임 시작) | `AbilityCooldownStartEvent` |
| `AbilityCooldownEndData` | (쿨타임 종료) | `AbilityCooldownEndEvent` |
| `AbilityDurationStartData` | (지속 시작) | `AbilityDurationStartEvent` |
| `AbilityDurationEndData` | (지속 종료) | `AbilityDurationEndEvent` |
| `AbilityDrawCompleteData` | (추첨 완료) | `AbilityDrawCompleteEvent` |

---

## 녹화 → 재생 흐름

```
녹화 중 (GameReplayApi...recordManager.isRecording())
   능력 이벤트 발생 → *DataRecordListener
      → *Data 생성 (원시값 저장)
      → record.addRecordData(현재 틱)

재생 중
   해당 틱 도달 → type 으로 핸들러 조회
      → *DataReplayHandler.onPlay(data, viewer)   // 관전자마다 호출
```

---

## 새 데이터 추가하기

`AbilityAssign` 한 세트가 가장 단순한 참고 예제입니다.
3종 세트를 만들고 `AbilityReplayHandlerRegistry`에 등록하면 됩니다.

1. **Data** : `data class` + `RecordData`, 모든 필드 `var`+기본값, 고유 `type`, 원시값만 저장
2. **RecordListener** : `isRecording()` 가드 → 이벤트를 Data로 → `record!!.addRecordData()`
3. **ReplayHandler** : `RecordDataReplayHandler<Data>`, `onPlay`에서 `viewer`에게 표현
4. **등록** : `AbilityReplayHandlerRegistry.registerHandlers()`에 `registerRecordData(...)` 추가

---

## 빌드

```bash
# 전체 빌드
./gradlew build

# 이 모듈만 shaded JAR (결과물: build/libs)
./gradlew :ability-replay:shadowJar

# 클린 후 전체 검증
./gradlew clean build
```

- JVM 타깃 `1.8`.
- 서버에는 `Core`, `GameReplay`, (그리고 능력 녹화를 위해) `Ability` 플러그인이 함께 설치되어야 합니다.
- 로컬 반영용 `copyToServerWindows` 태스크 경로는 개인 환경 고정이므로 사용 전 확인하세요.

---

## 참고 문서

- [ability-core README](../ability-core/README.md)
- 저장소 전체 규칙: 루트 `CLAUDE.md`
