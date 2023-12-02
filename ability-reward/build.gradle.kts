import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("maven-publish")
}

repositories {
}

dependencies {
    compileOnly(project(":ability-core"))
    compileOnly("net.pooleaf:money:1.0.0")
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

    register<Copy>("copyToServerWindows") {
        from(shadowJar)
//        into("D:\\서버\\1.8.9 테스트 서버\\update")
//        into("D:\\서버\\1.8.9 LeafServer S6\\lobby.1\\update")
        into("D:\\서버\\1.8.9 LeafServer S6\\replay.1\\update")
//        into("D:\\서버\\1.8.9 LeafServer S6\\city.ability.pf.1\\update")
    }
}