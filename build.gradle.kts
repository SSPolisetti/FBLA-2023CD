import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.0"
    id("org.jetbrains.compose")


}

//kotlin.code.style=official
//kotlin.version=1.7.20
//agp.version=7.0.4
//compose.version=1.2.1
//kotlinx-serialization.version = 1.5.0
//supabase-kt.version = 0.7.6
//ktor_version = 2.0.3


group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap")


}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("io.github.jan-tennert.supabase:postgrest-kt:0.7.6")
                implementation("io.ktor:ktor-client-cio:2.2.1")
                implementation("com.arkivanov.decompose:decompose:1.0.0")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:1.0.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")





            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "Main"
        nativeDistributions {
            windows {
                iconFile.set(project.file("icon.ico"))
            }
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "FBLA_CD"
            packageVersion = "1.0.0"
        }
    }
}
