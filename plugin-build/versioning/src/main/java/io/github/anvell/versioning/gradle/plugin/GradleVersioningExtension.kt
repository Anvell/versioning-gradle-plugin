package io.github.anvell.versioning.gradle.plugin

import io.github.anvell.versioning.gradle.plugin.actions.VcsActions
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

interface GradleVersioningExtension {
    val actions: Property<VcsActions>
    val remote: Property<String>
    val autoPush: Property<Boolean>
    val variants: SetProperty<String>
    val branches: SetProperty<Regex>
}
