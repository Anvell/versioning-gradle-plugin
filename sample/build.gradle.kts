@file:Suppress("DSL_SCOPE_VIOLATION", "SpellCheckingInspection")


plugins {
    id("java-library")
    id("io.github.anvell.versioning.gradle.plugin")
}

configureVersioning {
    autoPush.set(false)
    variants.set(setOf("", "dev"))
    versionCatalog.set(file("sample.versions.toml"))
}
