plugins {
    kotlin("multiplatform") version "1.5.10"
}

repositories {
    mavenCentral()
}

kotlin {
    macosX64("chapter1") { // on macOS
        // linuxX64("native") // on Linux
        // mingwX64("native") // on Windows
        compilations["main"].cinterops {
            val sdl by creating {
                if (file("/usr/local/include/SDL").exists()) {
                    includeDirs("/usr/local/include/SDL")
                } else {
                    includeDirs("/usr/local/include/SDL2")
                }
                includeDirs.headerFilterOnly("/usr/local/include")
            }
        }
        compilations["main"].enableEndorsedLibs = true
        binaries {
            executable {
                entryPoint = "chapter1.main"
            }
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.2")
            }
        }
    }
}

buildscript {
    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:10.1.0")
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "6.7.1"
    distributionType = Wrapper.DistributionType.BIN
}

apply {
    plugin("org.jlleitschuh.gradle.ktlint")
    from("ktlint.gradle.kt")
}
