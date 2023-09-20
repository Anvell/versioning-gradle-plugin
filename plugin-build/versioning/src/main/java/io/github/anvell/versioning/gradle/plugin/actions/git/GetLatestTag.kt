@file:Suppress("SpellCheckingInspection")

package io.github.anvell.versioning.gradle.plugin.actions.git

import io.github.anvell.versioning.gradle.plugin.actions.runCommand
import org.gradle.api.GradleException

internal fun getLatestTag(): String = runCommand(
    "git tag --sort=-committerdate | head -n 1"
).ifEmpty {
    throw GradleException("Error reading current tag")
}
