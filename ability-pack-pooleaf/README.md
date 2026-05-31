# ability-pack-pooleaf

풀잎서버 전용 `ability-core` 기반 능력팩입니다.
플러그인 이름은 `PooleafAbility`입니다.

## 역할

- `ability-core`의 `Ability`를 상속한 풀잎서버 전용 능력 클래스 모음
- 플러그인 시작 시 `AbilityApi.unsafe.abilityManager.registerAbilities(this)`로 능력 일괄 등록

## 의존 관계

```
Core, Ability, GameCore
        ▲
        │ compileOnly: ability-core
   ability-pack-pooleaf
```

## 구조

```
src/main/kotlin/net/pooleaf/ability/pack/pooleaf/
├── PooleafAbilityPlugin.kt
└── abilities/
```

능력 클래스는 `abilities/` 아래에 한 파일당 한 능력으로 추가합니다.
각 능력은 `init`에서 `pluginName / name / rank / type / description`을 반드시 채워야 자동 등록됩니다.

## 수록 능력

| 클래스 | 이름 | 등급 | 타입 |
| --- | --- | :---: | :---: |
| `BlockHideAndSeek` | 블럭숨바꼭질 | S | ACTIVE |
| `DeathNote` | 데스노트 | HIDDEN | PASSIVE |
| `FingerSnap` | 핑거스냅 | HIDDEN | ACTIVE |
| `Leap` | 도약 | A | ACTIVE |
| `Pigeon` | 비둘기 | HIDDEN | ACTIVE |
| `Recall` | 시간 역행 | SS | ACTIVE |
| `Repulsion` | 반발 | B | PASSIVE |
| `SpacetimeCollapse` | 시공간 붕괴 | SS | ACTIVE |
| `StaticElectricity` | 정전기 | B | PASSIVE |
| `Warden` | 워든 | HIDDEN | PASSIVE |
| `ZhonyaHourglass` | 존야의 모래시계 | B | ACTIVE |

## 빌드

```bash
./gradlew :ability-pack-pooleaf:build
./gradlew :ability-pack-pooleaf:shadowJar
```
