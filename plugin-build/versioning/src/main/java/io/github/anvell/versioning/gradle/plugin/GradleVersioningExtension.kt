package io.github.anvell.versioning.gradle.plugin

import io.github.anvell.versioning.gradle.plugin.actions.VcsActions
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

interface GradleVersioningExtension {
    /**
     * Allows to use custom VCS interop implementation.
     */
    val actions: Property<VcsActions>

    /**
     * Git remote name, default is 'origin'.
     */
    val remote: Property<String>

    /**
     * Whether to automatically push changes to the [remote].
     */
    val autoPush: Property<Boolean>

    /**
     * Specify custom tag variants e.g. environments, flavors etc.
     */
    val variants: SetProperty<String>

    /**
     * Restrict usage to specific branches if defined.
     */
    val branches: SetProperty<Regex>

    /**
     * Custom VCS message template, default is 'Version: %s'.
     */
    val commitTemplate: Property<String>

    /**
     * Specify version catalog file path. Note that plugin
     * will override this file after version increment.
     */
    val versionCatalog: RegularFileProperty
}
