import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kspigotVersion: String by project
val paperVersion: String by project

plugins {
    kotlin("jvm") version "1.9.21"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.papermc.paperweight.userdev") version "1.7.2"
}

group = "de.craftan"
version = "1.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
    implementation("net.axay:kspigot:$kspigotVersion")
}

paperweight {
    reobfArtifactConfiguration =
        io.papermc.paperweight.userdev
            .ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

bukkit {
    main = "de.craftan.InternalMain"
    version = rootProject.version.toString()
    description = "The settlers of Catan minecraft edition"
    authors = listOf("StaticFX")

    generateLibrariesJson = true
    apiVersion = "1.19"

    libraries = listOf("net.axay:kspigot:$kspigotVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    withType<KotlinCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
}
