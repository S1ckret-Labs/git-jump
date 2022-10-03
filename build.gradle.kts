import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform") version "1.7.10"
    application
}

group = "com.s1ckret.labs"
version = "0.0.1"

repositories {
    mavenCentral()
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val osName = when {
        HostManager.hostIsLinux -> Family.LINUX
        HostManager.hostIsMac -> Family.IOS
        HostManager.hostIsMingw -> Family.MINGW
        else -> error("unknown host")
    }

    if (HostManager.hostIsLinux) linuxX64()
    if (HostManager.hostIsMac) macosX64()
    if (HostManager.hostIsMingw) mingwX64()

    jvm {
        withJava()
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("com.github.ajalt.clikt:clikt:3.5.0")
                implementation("io.arrow-kt:arrow-core:1.1.2")
            }
        }
        val commonTest by getting
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
            }
        }
        val jvmTest by getting
    }
}

application {
    mainClass.set("com.s1ckret.labs.gitj.MainKt")
}

distributions {
    main {
        contents {
            from("$buildDir/libs") {
                rename("${rootProject.name}-jvm", rootProject.name)
                into("lib")
            }
        }
    }
}
