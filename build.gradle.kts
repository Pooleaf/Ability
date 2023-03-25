plugins {
    kotlin("jvm") version "1.8.10"
}

allprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()

        maven("https://repo.s8u.kr/repository/maven-minecraft/") // Bukkit
        maven("https://repo.s8u.kr/repository/maven-pooleaf/") // Core
        maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
        maven("https://repo.glaremasters.me/repository/concuncan/") // SWM
        maven("https://maven.citizensnpcs.co/repo") // Citizens
    }

    dependencies {
        compileOnly("io.papermc:paper:1.8.8")

        compileOnly("net.pooleaf:core:1.50.2")
        compileOnly("net.pooleaf:game-core:1.1.1")
        compileOnly("net.pooleaf:game-replay:1.1.0")

        compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")

        compileOnly("Xeon:VisualAbility:2.0-s8u") // 비트 능력자
        compileOnly("Physical:Fighters:1.0.0") // 염료 능력자
//    compileOnly("daybreak:abilitywar:2.1.6.8") // AbilityWar

        compileOnly("com.grinderwolf:slimeworldmanager-api:2.2.1")
    }
}