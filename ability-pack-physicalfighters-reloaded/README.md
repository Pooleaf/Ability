# ability-pack-physicalfighters-reloaded

풀잎서버 **PhysicalFighters용 능력 구현 모음**(능력팩)입니다.
`ability-core` 위에서 동작하는 개별 능력들을 구현하고, 플러그인이 켜질 때 코어에 등록합니다.

> 능력팩 제작 방법 일반 가이드 → [`ability-core/docs/creating-ability-pack.md`](../ability-core/docs/creating-ability-pack.md)
> 이 README는 **이 팩 고유의 구성**만 다룹니다.

---

## 역할

- `ability-core`의 `Ability`를 상속한 능력 클래스 모음 (액티브/패시브)
- 플러그인 시작 시 `registerAbilities(this)`로 능력 일괄 등록
- 등록 직후, **같은 이름의 PhysicalFighters 원본 능력을 밴 처리**(중복 방지)

이름이 "reloaded"인 이유: 기존 PhysicalFighters 플러그인의 능력을 `ability-core` 기반으로 다시 구현한 팩이기 때문입니다.

---

## 의존 관계

```
Core, Ability, GameCore   ← 필수 (depend)
        ▲
        │ compileOnly: ability-core, net.pooleaf:money
   ability-pack-physicalfighters-reloaded
```

`plugin.yml`:

```yaml
name: $pluginName
depend:
  - Core
  - Ability       # ability-core (능력 시스템)
  - GameCore
```

---

## 진입점

`PhysicalFightersReloadedPlugin` (`BukkitCorePlugin`):

```kotlin
override fun onStart() {
    instance = this
    prefix = "§c[ PhysicalFightersReloaded ]"
    // ...

    // 이 플러그인의 모든 Ability 자동 등록
    AbilityApi.unsafe.abilityManager.registerAbilities(this)

    // 같은 이름의 PhysicalFighters 원본 능력은 밴 처리 (중복 방지)
    val pfName = PhysicalFightersCompatPlugin().name
    AbilityApi.unsafe.abilityManager.getAbilities()
        .filter { it.pluginName == this.name }
        .forEach {
            AbilityApi.unsafe.abilityManager
                .getAbilityByFullName("${pfName}:${it.name}")?.ban = true
        }
}
```

`registerAbilities(this)`는 JAR 안에서 `Ability`를 상속하고 초기화가 끝난(`isInitialized`) 클래스를 리플렉션으로 모두 등록합니다. 따라서 능력 클래스의 `init {}`에서 `pluginName / name / rank / type / description`을 반드시 채워야 합니다.

---

## 구조

```
abilities/                  ← 능력 클래스 (한 파일 = 한 능력)
PhysicalFightersReloadedPlugin.kt
```

새 능력을 추가하려면 `abilities/`에 클래스를 하나 만들면 끝입니다. 등록은 리플렉션이 자동으로 처리합니다.

---

## 수록 능력 (24종)

| 클래스 | 이름 | 등급 | 타입 |
| --- | --- | :---: | :---: |
| `Angle` | 천사 | SS | ACTIVE |
| `Cuma` | 바솔로뮤 쿠마 | SS | PASSIVE |
| `Gongban` | 공격반사 | SS | ACTIVE |
| `Pagi` | 패기 | SS | ACTIVE |
| `RingOfIsotar` | 이슈타르의 링 | SS | ACTIVE |
| `Fly` | 플라이 | S | ACTIVE |
| `Mirroring` | 미러링 | S | PASSIVE |
| `ReverseAlchemy` | 반 연금술 | S | ACTIVE |
| `Time` | 타임 | S | ACTIVE |
| `Aegis` | 이지스 | A | ACTIVE |
| `Anorexia` | 거식증 | A | PASSIVE |
| `Clocking` | 클로킹 | A | ACTIVE |
| `ExplosionGlove` | 폭파장갑 | A | ACTIVE |
| `ExplosionPa` | 기공파 | A | ACTIVE |
| `Gaara` | 가아라 | A | ACTIVE |
| `MultiShot` | 멀티샷 | A | PASSIVE |
| `Ninja` | 닌자 | A | ACTIVE |
| `Nonuck` | 무통증 | A | PASSIVE |
| `ThunderBolt` | 썬더볼트 | A | ACTIVE |
| `Yasuo` | 야스오 | A | PASSIVE |
| `Zoro` | 조로 | A | ACTIVE |
| `Explosion` | 익스플로젼 | B | PASSIVE |
| `Zombie` | 좀비 | B | PASSIVE |
| `Nasus` | 나서스 | C | ACTIVE |

> 표는 각 능력 클래스의 `init` 메타데이터에서 추출한 값입니다. 능력을 추가/수정하면 함께 갱신하세요.

---

## 새 능력 추가하기

`abilities/`에 `Ability`를 상속한 클래스를 만들고 발동 방식에 맞는 인터페이스를 구현합니다.

```kotlin
class ThunderBolt : Ability(), Listener, CastByItemHandler, Cooldownable {
    init {
        pluginName = PhysicalFightersReloadedPlugin.instance.name
        name = "썬더볼트"
        rank = AbilityRank.A
        type = AbilityType.ACTIVE
        description = listOf("철괴 클릭 시 5칸 내 적에게 6 데미지를 줍니다.")
        ban = false
    }

    override val castItem = listOf(ItemStack(Material.IRON_INGOT))
    override val cooldownTimer = CoolDownTimer(this, 5_000L)

    override fun onCastByItem(event: PlayerInteractEvent, item: ItemStack,
                              clickType: CastByItemHandler.ClickType): Boolean {
        event.player.getNearbyEntities(5.0, 5.0, 5.0).forEach { e ->
            if (e !is LivingEntity) return@forEach
            event.player.world.strikeLightningEffect(e.location)
            e.damageBypassAntiCheat(6.0, event.player)
        }
        return true
    }
}
```

발동 방식별 인터페이스(아이템 액티브 / 지속형 / 패시브)와 타이머·코루틴 주의사항 등 자세한 내용은
**[`ability-core/docs/creating-ability-pack.md`](../ability-core/docs/creating-ability-pack.md)** 를 참고하세요.

---

## 빌드

```bash
# 전체 빌드
./gradlew build

# 이 모듈만 shaded JAR (결과물: build/libs)
./gradlew :ability-pack-physicalfighters-reloaded:shadowJar

# 클린 후 전체 검증
./gradlew clean build
```

- JVM 타깃 `1.8`. `ability-core`는 `compileOnly`이므로 JAR에 포함되지 않습니다.
- 서버에는 `Core`, `Ability`, `GameCore` 플러그인과 이 팩 JAR이 함께 설치되어야 합니다.
- `money` 모듈을 `compileOnly`로 사용합니다(일부 능력/보상 연동).
- 로컬 반영용 `copyToServerWindows` 태스크 경로는 개인 환경 고정이므로 사용 전 확인하세요.

---

## 참고 문서

- [능력팩 만들기 가이드](../ability-core/docs/creating-ability-pack.md)
- [ability-core README](../ability-core/README.md)
- 저장소 전체 규칙: 루트 `CLAUDE.md`
