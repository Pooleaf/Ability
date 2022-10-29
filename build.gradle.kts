import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenCentral()

    maven("https://repo.s8u.kr/repository/maven-minecraft/") // Bukkit
    maven("https://repo.s8u.kr/repository/maven-pooleaf/") // Core
    maven("https://repo.glaremasters.me/repository/concuncan/") // SWM
}

dependencies {
    compileOnly("io.papermc:paper:1.8.8")
    compileOnly("net.pooleaf:core:0.0.25")

    compileOnly("Xeon:VisualAbility:2.0-s8u") // 비트 능력자
    compileOnly("Physical:Fighters:1.0.0") // 염료 능력자
//    compileOnly("daybreak:abilitywar:2.1.6.8") // AbilityWar

    compileOnly("com.grinderwolf:slimeworldmanager-api:2.2.1")

    testImplementation(kotlin("test"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
    }

    withType<ShadowJar> {
        archiveClassifier.set("")
    }

    register<Copy>("copyToServerWindows") {
        from(shadowJar)
        into("D:\\서버\\1.8.9 테스트 서버\\update")
    }
}