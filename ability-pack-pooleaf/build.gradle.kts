import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("maven-publish")
}

dependencies {
    compileOnly(project(":ability-core"))

    // 시공간 붕괴 그림자 NPC (저장소는 루트 build.gradle.kts에 등록됨)
    compileOnly("net.citizensnpcs:citizens:2.0.24")
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
        delete("build/resources")

        archiveClassifier.set("")
    }
}
