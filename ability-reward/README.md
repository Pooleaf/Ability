# ability-reward

풀잎서버 **능력 게임 보상 모듈**입니다.
능력 게임에서 발생하는 **킬 / 연속킬 / 어시스트 / 우승**에 대해 게임머니를 자동 지급합니다.
지급액은 설정 파일로 조정하며, 우승 보상은 **JavaScript 수식**으로 동적 계산합니다.

---

## 역할

- 게임 이벤트(킬·우승)를 듣고 보상 게임머니를 계산·지급
- 보상 금액을 `ability-reward-config.yml`로 설정
- 우승 보상은 JS 수식(`winMoneyFormula`)으로 인원수 기반 동적 계산
- 지급은 `money` 모듈(`MoneyApi`)을 통해 처리

---

## 의존 관계

```
Core, Ability, GameCore   ← 필수 (depend)
        ▲
        │ compileOnly: ability-core, net.pooleaf:money
   ability-reward
```

`plugin.yml`:

```yaml
name: $pluginName
depend:
  - Core
  - Ability
  - GameCore
```

`GameCore`의 게임 이벤트와 플레이어/팀 정보, `money` 모듈의 `MoneyApi`에 의존합니다.

---

## 구조

```
AbilityRewardPlugin          진입점 (BukkitCorePlugin)
AbilityRewardApi             퍼사드 (config / service / js)
configs/
└── AbilityRewardConfig      보상 금액 설정 (SimpleAnnoConfig)
listeners/
└── AbilityRewardListener    킬/우승 이벤트 → 보상 지급
services/
├── AbilityRewardService     보상 계산·지급 로직
└── JavaScriptService        우승 수식 평가용 JS 엔진 래퍼
```

### 진입점

- **`AbilityRewardPlugin`** — `onStart()`에서 `AbilityRewardApi.init()` + `registerEventListeners()`. `onConfigLoaded()`에서 설정을 리로드합니다.
- **`AbilityRewardApi`** — `abilityRewardConfig`, `abilityRewardService`, `javaScriptService`를 보유. `reloadConfig()`가 설정을 다시 읽고, JS 엔진을 초기화한 뒤 우승 수식 함수를 다시 등록합니다.

---

## 보상 동작

`AbilityRewardListener`가 두 이벤트를 처리합니다.

| 이벤트 | 보상 |
| --- | --- |
| `GamePlayerDefeatEvent` (킬 발생) | 킬러에게 **킬** 보상, `useKillStreak`이면 **연속킬** 보상, 어시스트 플레이어에게 **어시스트** 보상 |
| `GameEndEvent` (게임 종료) | 우승 팀 전원에게 **우승** 보상 |

지급 시 플레이어에게 `+ N원 (사유)` 메시지와 효과음을 보내고, 실제 지급(`MoneyApi.addMoney`)은 비동기로 처리합니다.

---

## 설정 (`ability-reward-config.yml`)

`GameCore.gamePlugin.dataFolder` 아래에 생성됩니다. (`@ConfigName`은 설정 파일의 한글 키)

| 필드 | 설정 키 | 기본값 |
| --- | --- | --- |
| `killMoney` | 킬 게임머니 | `100.0` |
| `doubleKillMoney` | 더블킬 게임머니 | `100.0` |
| `tripleKillMoney` | 트리플킬 게임머니 | `150.0` |
| `quadraKillMoney` | 쿼드라킬 게임머니 | `200.0` |
| `pentaKillMoney` | 펜타킬 게임머니 | `300.0` |
| `assistMoney` | 어시스트 게임머니 | `10.0` |
| `winMoneyFormula` | 우승 게임머니 | `startPlayerCount * 100 / teamPlayerCount` |

- 각 금액이 `0` 이하이면 해당 보상은 지급하지 않습니다.
- 연속킬 보상은 `GameCore.gameConfig.useKillStreak`이 켜져 있을 때만 지급됩니다.

---

## 우승 보상 수식 (JavaScript)

`winMoneyFormula`는 다음 시그니처의 JS 함수 본문으로 평가됩니다.

```js
function calculateWinMoney(startPlayerCount, startTeamCount, teamPlayerCount) {
    return <winMoneyFormula>
}
```

사용 가능한 변수:

| 변수 | 의미 |
| --- | --- |
| `startPlayerCount` | 게임 시작 시 참가 인원 수 |
| `startTeamCount` | 게임 시작 시 팀 개수 |
| `teamPlayerCount` | 우승한 팀의 인원 수 |

- 계산 결과는 `floor`로 소수점을 버린 뒤 지급됩니다.
- 평가는 JDK 내장 JS 엔진(`ScriptEngineManager`, Nashorn 계열)을 사용합니다.
- 설정 리로드(`reloadConfig`) 시 JS 엔진이 초기화되고 수식이 다시 등록됩니다.

예시:

```yaml
# 시작 인원 비례, 적은 팀일수록 1인당 보상 증가 (기본값)
우승 게임머니: "startPlayerCount * 100 / teamPlayerCount"

# 고정 500원
우승 게임머니: "500"

# 팀 수에 비례
우승 게임머니: "startTeamCount * 200"
```

> ⚠️ 수식은 신뢰된 운영자만 편집하는 설정값입니다. 임의 사용자 입력을 그대로 넣지 마세요(코드 실행).

---

## 빌드

```bash
# 전체 빌드
./gradlew build

# 이 모듈만 shaded JAR (결과물: build/libs)
./gradlew :ability-reward:shadowJar

# 클린 후 전체 검증
./gradlew clean build
```

- JVM 타깃 `1.8`.
- 서버에는 `Core`, `Ability`, `GameCore`, `money`(MoneyApi 제공) 플러그인과 이 모듈이 함께 설치되어야 합니다.
- 로컬 반영용 `copyToServerWindows` 태스크 경로는 개인 환경 고정이므로 사용 전 확인하세요.

---

## 참고 문서

- [ability-core README](../ability-core/README.md)
- 저장소 전체 규칙: 루트 `CLAUDE.md`
