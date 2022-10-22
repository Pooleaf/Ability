import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "net.pooleaf"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://repo.s8u.kr/repository/maven-minecraft/")
    maven("https://repo.s8u.kr/repository/maven-pooleaf/")
    maven("https://repo.glaremasters.me/repository/concuncan/")
}

dependencies {
    compileOnly("io.papermc:paper:1.8.8")
    compileOnly("net.pooleaf:core:0.0.23")

    compileOnly("Xeon:VisualAbility:2.0-s8u") // 비트 능력자
    compileOnly("Physical:Fighters:1.0.0") // 염료 능력자
    compileOnly("daybreak:abilitywar:2.1.6.8") // AbilityWar

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