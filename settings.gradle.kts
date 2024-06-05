@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }

    versionCatalogs {
        create("projectVersioning") {
            from(files("gradle/project.versions.toml"))
        }
    }
}

rootProject.name = "Versioning Gradle Plugin"

includeBuild("plugin-build")
