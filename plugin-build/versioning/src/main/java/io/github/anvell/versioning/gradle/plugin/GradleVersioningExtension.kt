package io.github.anvell.versioning.gradle.plugin

import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

interface GradleVersioningExtension {
    val remote: Property<String>
    val autoPush: Property<Boolean>
    val branches: MapProperty<String, String>
}
