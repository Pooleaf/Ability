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
        // Kotlin
        compileOnly(kotlin("stdlib"))

        // Bukkit
        compileOnly("io.papermc:paper:1.8.8")

        // Core
        compileOnly("net.pooleaf:core:latest.integration")
        compileOnly("net.pooleaf:game-core:latest.integration")
        compileOnly("net.pooleaf:game-replay:latest.integration")

        // Bukkit Library
        compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")

        compileOnly("Xeon:VisualAbility:2.0-s8u") // 비트 능력자
        compileOnly("Physical:Fighters:1.0.0") // 염료 능력자
//    compileOnly("daybreak:abilitywar:2.1.6.8") // AbilityWar

        compileOnly("com.grinderwolf:slimeworldmanager-api:2.2.1")
    }
}