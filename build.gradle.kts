import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kspigotVersion: String by project
val paperVersion: String by project
val scoreboardLibraryVersion: String by project
val jupiterVersion: String by project
val kotlinVersion = "2.2.0"

plugins {
    kotlin("jvm") version "2.2.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.gradleup.shadow") version "9.2.2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    //id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "de.craftan"
version = "1.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven("https://jitpack.io")
    mavenLocal()
}

dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

    implementation("net.axay:kspigot:$kspigotVersion")

    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.10")
    implementation("com.uchuhimo:konf:1.1.2")
    implementation("net.ormr.eventbus:eventbus-core:0.2.0")

    implementation(platform("com.intellectualsites.bom:bom-newest:1.48")) // Ref: https://github.com/IntellectualSites/bom
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit") { isTransitive = false }

    implementation("io.github.staticfx:kia:1.1.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    compileOnly("net.megavex:scoreboard-library-api:$scoreboardLibraryVersion")
    compileOnly("net.megavex:scoreboard-library-extra-kotlin:$scoreboardLibraryVersion")

    //tests
    testImplementation(kotlin("test"))
}

bukkit {
    main = "de.craftan.InternalMain"
    version = rootProject.version.toString()
    description = "The settlers of Catan minecraft edition"
    authors = listOf("StaticFX", "Dawitschi")

    generateLibrariesJson = true
    apiVersion = "1.21"

    libraries = listOf(
        "net.axay:kspigot:$kspigotVersion",
        "net.megavex:scoreboard-library-implementation:$scoreboardLibraryVersion",
        "net.megavex:scoreboard-library-modern:$scoreboardLibraryVersion",
    )
    depend = listOf("FastAsyncWorldEdit")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test>().configureEach {
    testLogging {
        events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
        )
        showExceptions = true
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    withType<KotlinCompile> {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }

    shadowJar {
        //relocate("net.megavex.scoreboardlibrary", "de.craftan.libs.scoreboardlibrary")
    }

    build {
        dependsOn(shadowJar)
    }
}
