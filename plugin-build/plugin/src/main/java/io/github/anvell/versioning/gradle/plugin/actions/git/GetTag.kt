package io.github.anvell.versioning.gradle.plugin.actions.git

import io.github.anvell.versioning.gradle.plugin.actions.runCommand
import org.gradle.api.GradleException

internal fun getTag(): String {
    val revList = runCommand("git rev-list --tags --max-count=1")
    val result = runCommand("git describe --tags $revList")

    return result.ifEmpty {
        throw GradleException("Error reading current tag")
    }
}
