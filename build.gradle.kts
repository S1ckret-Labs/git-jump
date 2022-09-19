import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "com.s1ckret.labs"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.s1ckret.labs.MainKt")
}

val uberJar = tasks.register<Jar>("uberJar") {
    this.group = "build"
    this.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveClassifier.set("uber")

    manifest {
        attributes["Main-Class"] = application.mainClass
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    with(tasks.jar.get() as CopySpec)
}

tasks {
    "build" {
        dependsOn(uberJar)
    }
}
