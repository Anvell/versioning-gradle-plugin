package io.github.anvell.versioning.gradle.plugin.actions.git

import io.github.anvell.versioning.gradle.plugin.actions.runCommand

internal fun getBranchName() = runCommand("git rev-parse --abbrev-ref HEAD")
