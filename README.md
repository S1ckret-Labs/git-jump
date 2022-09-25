# Git Jump

## How to build

```shell
./gradlew clean build
```

## How to launch

```shell
# From the project root folder
cd build/distributions/
tar -xvf ./gitj-0.0.1.tar
./git-jump-0.0.1/bin/git-jump
```

## Kotlin Multiplatform encountered problems

### Build related

1. It doesn't build uberJar (fatJar) by default
2. It doesn't build zip/tar with dependencies by default
3. Resulted jar doesn't have `Main-Class`, `Class-Path` attributes
4. Since the project doesn't have `java` plugin we can't apply "default" solutions
5. Cannot change dependencies of dependency configuration ':jvmApi' after it has been included in dependency resolution.

#### Solution

I decided to build distribution rather than uberJar because in that way I don't override manifests in dependent libraries.
But I am sure we can easily create uberJar from those pieces.

**UPD:** I switched to `application` plugin since it does what I want.

1. Create a distribution

The first into block copies your jar that is build by Kotlin Multiplatform `jvmJar` task.
The second into block copies your runtime dependency files. (Analogue to configurations.runtimeClasspath.get() from `java` plugin)

```kotlin
distributions {
    main {
        distributionBaseName.set("gitj")
        contents {
            into("") {
                val jvmJar by tasks.getting
                from(jvmJar)
            }
            into("libs/") {
                val main by kotlin.jvm().compilations.getting
                from(main.runtimeDependencyFiles)
            }
        }
    }
}
```

On this step we can launch our application with this command

```shell
java -cp 'git-jump-jvm-0.0.1.jar:libs/*' com.s1ckret.labs.gitj.MainKt
```

2. Add `Main-Class` attribute to manifest

```kotlin
tasks.withType<Jar> {
    manifest {
        attributes(
            "Main-Class" to "com.s1ckret.labs.gitj.MainKt",
        )
    }
}
```

On this step we can launch our application with this command

```shell
java -cp 'git-jump-jvm-0.0.1.jar:libs/*' com.s1ckret.labs.gitj.MainKt
```

3. Add `Main-Class` and `Class-Path` attributes to manifest

It is very important to wrap with `doFirst` function, otherwise problem #5 is showing up

```kotlin
tasks.withType<Jar> {
    doFirst {
        manifest {
            val main by kotlin.jvm().compilations.getting
            attributes(
                "Main-Class" to "com.s1ckret.labs.gitj.MainKt",
                "Class-Path" to main.runtimeDependencyFiles.files.joinToString(" ") { "libs/" + it.name }
            )
        }
    }
}
```

On this step we can launch our application with this command

```shell
java -jar git-jump-jvm-0.0.1.jar
```

#### Links

- https://stackoverflow.com/questions/22659463/add-classpath-in-manifest-using-gradle
- https://stackoverflow.com/questions/42552511/cannot-change-dependencies-of-configuration-compile-after-it-has-been-resolve
- https://stackoverflow.com/questions/15930782/call-java-jar-myfile-jar-with-additional-classpath-option
- https://stackoverflow.com/questions/44197521/gradle-project-java-lang-noclassdeffounderror-kotlin-jvm-internal-intrinsics
- https://stackoverflow.com/questions/56921833/kotlin-program-error-no-main-manifest-attribute-in-jar-file
- https://stackoverflow.com/questions/68561300/kotlin-multiplatform-library-unresolved-dependency-at-runtime
- https://stackoverflow.com/questions/25398703/gradle-analogue-for-maven-assembly-plugin
- https://docs.gradle.org/current/userguide/working_with_files.html#sec:creating_uber_jar_example
- https://docs.gradle.org/current/userguide/distribution_plugin.html

#### Contributed

Answered to [How to execute Kotlin Multiplatform Jar](https://stackoverflow.com/questions/73778977/how-to-execute-kotlin-multiplatform-jar)
