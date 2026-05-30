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

## 빌드

```bash
./gradlew :ability-pack-pooleaf:build
./gradlew :ability-pack-pooleaf:shadowJar
```
