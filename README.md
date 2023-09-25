# Versioning Gradle Plugin

Calculate and apply automatic [CalVer](https://calver.org) commits and tags to your project.

## Setup

Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):

``` kotlin
plugins {
    id("io.github.anvell.versioning.gradle.plugin") version "2023.09.01"
}
```

## How to use
### Basic configuration

``` kotlin
configureVersioning {
    // Default is 'origin'
    remote.set("your-remote-name")

    // Set 'true' to push automatically
    autoPush.set(true)

    // Specify version catalog to use
    versionCatalog.set(file("gradle/project.versions.toml"))
}
```

### Create version commit

In order to create new version launch Gradle task:

```shell
$ ./gradlew publishVersion
```

This will increment latest version from version catalog found in VCS history according to [CalVer](https://calver.org) rules, create commit and optionally push it to the remote.

### Create version tag

```shell
$ ./gradlew publishVersionTag
```

Plugin will attempt to parse latest tag and increment version according to [CalVer](https://calver.org) rules. Using different `variants` allows to tag the same version for different environments.

## Contributing

Feel free to open a issue or submit a pull request for any bugs/improvements.

## Credits

This project uses [Kotlin gradle plugin template](https://github.com/cortinico/kotlin-gradle-plugin-template).
