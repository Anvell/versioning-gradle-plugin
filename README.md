# Versioning Gradle Plugin

Calculate and apply automatic [CalVer](https://calver.org) tags to your project.

## Setup

Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):

``` kotlin
plugins {
    id("io.github.anvell.versioning.gradle.plugin") version "0.1.0"
}
```

## How to use
### Configure extension

``` kotlin
configureVersioning {
    remote.set("your-remote-name") // Default is 'origin'
    autoPush.set(true) // Set 'true' to push tags automatically
    branches.put("develop", "dev") // Set allowed branches with specific suffixes
}
```

### Apply version tag

Plugin attempts to parse latest tag and increment version according to [CalVer](https://calver.org) rules.

```shell
$ ./gradlew publishVersionTag
```

## Contributing

Feel free to open a issue or submit a pull request for any bugs/improvements.

## Credits

This project uses [Kotlin gradle plugin template](https://github.com/cortinico/kotlin-gradle-plugin-template).
