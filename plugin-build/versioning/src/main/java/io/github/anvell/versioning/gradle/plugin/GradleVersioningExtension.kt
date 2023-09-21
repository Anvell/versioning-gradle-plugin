package io.github.anvell.versioning.gradle.plugin

import io.github.anvell.versioning.gradle.plugin.actions.VcsActions
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

interface GradleVersioningExtension {
    val actions: Property<VcsActions>
    val remote: Property<String>
    val autoPush: Property<Boolean>
    val branches: MapProperty<String, String>
}
