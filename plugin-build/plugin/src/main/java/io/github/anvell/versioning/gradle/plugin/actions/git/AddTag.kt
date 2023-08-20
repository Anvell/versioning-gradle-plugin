package io.github.anvell.versioning.gradle.plugin.actions.git

import io.github.anvell.versioning.gradle.plugin.actions.runCommand

internal fun addTag(tag: String) = runCommand("git tag $tag")
